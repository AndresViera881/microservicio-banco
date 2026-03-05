package com.banco.movimiento.service.impl;

import com.banco.movimiento.client.ClienteClient;
import com.banco.movimiento.client.CuentaClient;
import com.banco.movimiento.dto.ReporteDTO;
import com.banco.movimiento.exception.ResourceNotFoundException;
import com.banco.movimiento.model.Movimiento;
import com.banco.movimiento.repository.MovimientoRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl {

    private final MovimientoRepository movimientoRepository;
    private final CuentaClient cuentaClient;
    private final ClienteClient clienteClient;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ReporteDTO generarReporte(String clienteId, LocalDate fechaInicio, LocalDate fechaFin) {

        ClienteClient.ClienteInfo cliente = clienteClient.buscarClientePorId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "clienteId", clienteId));

        List<CuentaClient.CuentaInfo> cuentas = cuentaClient.obtenerCuentasDelCliente(clienteId);

        if (cuentas.isEmpty()) {
            throw new ResourceNotFoundException("Cuenta", "clienteId", clienteId);
        }

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<ReporteDTO.ReporteCuentaDTO> cuentasDTO = new ArrayList<>();

        for (CuentaClient.CuentaInfo cuenta : cuentas) {

            List<Movimiento> movimientos = movimientoRepository
                    .findByNumeroCuentaAndFechaRange(
                            cuenta.getNumeroCuenta(),
                            inicio,
                            fin
                    );

            BigDecimal totalCreditos = movimientos.stream()
                    .filter(m -> m.getValor().compareTo(BigDecimal.ZERO) > 0)
                    .map(Movimiento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDebitos = movimientos.stream()
                    .filter(m -> m.getValor().compareTo(BigDecimal.ZERO) < 0)
                    .map(Movimiento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoDisponible = movimientos.isEmpty()
                    ? cuenta.getSaldoInicial()
                    : movimientos.get(movimientos.size() - 1).getSaldo();

            List<ReporteDTO.ReporteMovimientoDTO> movimientosDTO =
                    movimientos.stream()
                            .map(m -> ReporteDTO.ReporteMovimientoDTO.builder()
                                    .fecha(m.getFecha())
                                    .tipoMovimiento(m.getTipoMovimiento())
                                    .valor(m.getValor())
                                    .saldoDisponible(m.getSaldo())
                                    .build())
                            .collect(Collectors.toList());

            cuentasDTO.add(
                    ReporteDTO.ReporteCuentaDTO.builder()
                            .numeroCuenta(cuenta.getNumeroCuenta())
                            .tipoCuenta(cuenta.getTipoCuenta())
                            .saldoInicial(cuenta.getSaldoInicial())
                            .estado(cuenta.getEstado())
                            .totalCreditos(totalCreditos)
                            .totalDebitos(totalDebitos)
                            .saldoDisponible(saldoDisponible)
                            .movimientos(movimientosDTO)
                            .build()
            );
        }

        return ReporteDTO.builder()
                .cliente(cliente.getNombre())
                .identificacionCliente(cliente.getIdentificacion())
                .cuentas(cuentasDTO)
                .reporteBase64(
                        generarPdf(cliente, cuentasDTO, fechaInicio, fechaFin)
                )
                .build();
    }

    private String generarPdf(
            ClienteClient.ClienteInfo cliente,
            List<ReporteDTO.ReporteCuentaDTO> cuentas,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(doc, baos);

            doc.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.DARK_GRAY);
            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

            Paragraph title = new Paragraph("ESTADO DE CUENTA BANCARIA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(4);
            doc.add(title);

            Paragraph subtitle = new Paragraph(
                    String.format("Período: %s — %s",
                            fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    ),
                    new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)
            );

            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(12);
            doc.add(subtitle);

            PdfPTable clienteTable = new PdfPTable(2);
            clienteTable.setWidthPercentage(100);
            clienteTable.setSpacingAfter(16);

            addRow(clienteTable, "Cliente:", cliente.getNombre(), labelFont, normalFont);
            addRow(clienteTable, "Identificación:", cliente.getIdentificacion(), labelFont, normalFont);

            doc.add(clienteTable);

            for (ReporteDTO.ReporteCuentaDTO cuenta : cuentas) {

                PdfPTable header = new PdfPTable(1);
                header.setWidthPercentage(100);

                PdfPCell cell = new PdfPCell(new Phrase(
                        "Cuenta: " + cuenta.getNumeroCuenta() +
                                " | Tipo: " + cuenta.getTipoCuenta() +
                                " | Saldo Inicial: $" + cuenta.getSaldoInicial(),
                        new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE)
                ));

                cell.setBackgroundColor(new Color(41, 128, 185));
                cell.setPadding(6);

                header.addCell(cell);
                header.setSpacingBefore(10);

                doc.add(header);

                if (!cuenta.getMovimientos().isEmpty()) {

                    PdfPTable movimientosTable = new PdfPTable(4);

                    movimientosTable.setWidthPercentage(100);

                    movimientosTable.setWidths(new float[]{2.5f, 1.5f, 1.5f, 1.5f});

                    String[] headers = {
                            "Fecha",
                            "Tipo",
                            "Valor",
                            "Saldo"
                    };

                    for (String h : headers) {

                        PdfPCell hc = new PdfPCell(new Phrase(h, headerFont));

                        hc.setBackgroundColor(new Color(52, 73, 94));
                        hc.setHorizontalAlignment(Element.ALIGN_CENTER);
                        hc.setPadding(5);

                        movimientosTable.addCell(hc);
                    }

                    for (ReporteDTO.ReporteMovimientoDTO mov : cuenta.getMovimientos()) {

                        movimientosTable.addCell(
                                cell(mov.getFecha().format(FMT), normalFont)
                        );

                        movimientosTable.addCell(
                                cell(mov.getTipoMovimiento(), normalFont)
                        );

                        movimientosTable.addCell(
                                cell(String.format("$%.2f", mov.getValor()), normalFont)
                        );

                        movimientosTable.addCell(
                                cell(String.format("$%.2f", mov.getSaldoDisponible()), normalFont)
                        );
                    }

                    doc.add(movimientosTable);
                }

                PdfPTable resumen = new PdfPTable(2);

                resumen.setWidthPercentage(50);
                resumen.setHorizontalAlignment(Element.ALIGN_RIGHT);

                resumen.setSpacingBefore(5);
                resumen.setSpacingAfter(10);

                addRow(resumen, "Total Créditos:", "$" + cuenta.getTotalCreditos(), labelFont, normalFont);
                addRow(resumen, "Total Débitos:", "$" + cuenta.getTotalDebitos(), labelFont, normalFont);
                addRow(resumen, "Saldo Disponible:", "$" + cuenta.getSaldoDisponible(), labelFont, normalFont);

                doc.add(resumen);
            }

            doc.close();

            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {

            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {

        PdfPCell l = new PdfPCell(new Phrase(label, labelFont));
        l.setBorder(Rectangle.NO_BORDER);
        l.setPadding(3);

        PdfPCell v = new PdfPCell(new Phrase(value != null ? value : "-", valueFont));
        v.setBorder(Rectangle.NO_BORDER);
        v.setPadding(3);

        table.addCell(l);
        table.addCell(v);
    }

    private PdfPCell cell(String text, Font font) {

        PdfPCell c = new PdfPCell(new Phrase(text, font));

        c.setPadding(4);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);

        return c;
    }
}
