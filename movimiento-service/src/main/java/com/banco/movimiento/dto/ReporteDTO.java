package com.banco.movimiento.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReporteDTO {
    private String cliente;
    private String identificacionCliente;
    private List<ReporteCuentaDTO> cuentas;
    private String reporteBase64;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ReporteCuentaDTO {
        private String numeroCuenta;
        private String tipoCuenta;
        private BigDecimal saldoInicial;
        private Boolean estado;
        private BigDecimal totalCreditos;
        private BigDecimal totalDebitos;
        private BigDecimal saldoDisponible;
        private List<ReporteMovimientoDTO> movimientos;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ReporteMovimientoDTO {
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        private LocalDateTime fecha;
        private String tipoMovimiento;
        private BigDecimal valor;
        private BigDecimal saldoDisponible;
    }
}
