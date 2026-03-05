package com.banco.movimiento.controller;

import com.banco.movimiento.dto.ApiResponse;
import com.banco.movimiento.dto.ReporteDTO;
import com.banco.movimiento.service.impl.ReporteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteServiceImpl reporteService;

    /**
     * GET /api/reportes?clienteId=CL002&fechaInicio=2022-01-01&fechaFin=2022-12-31
     * Retorna JSON con cuentas, movimientos, totales y PDF en Base64.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ReporteDTO>> generarReporte(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if (fechaFin.isBefore(fechaInicio))
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("La fecha fin no puede ser anterior a la fecha inicio"));

        return ResponseEntity.ok(ApiResponse.success("Reporte generado",
                reporteService.generarReporte(clienteId, fechaInicio, fechaFin)));
    }
}
