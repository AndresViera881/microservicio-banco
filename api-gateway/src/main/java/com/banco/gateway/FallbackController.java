package com.banco.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/clientes")
    public ResponseEntity<Map<String, Object>> clienteFallback() {
        return buildFallback("cliente-service");
    }

    @GetMapping("/cuentas")
    public ResponseEntity<Map<String, Object>> cuentaFallback() {
        return buildFallback("cuenta-service");
    }

    @GetMapping("/movimientos")
    public ResponseEntity<Map<String, Object>> movimientoFallback() {
        return buildFallback("movimiento-service");
    }

    @GetMapping("/reportes")
    public ResponseEntity<Map<String, Object>> reporteFallback() {
        return buildFallback("movimiento-service (reportes)");
    }

    private ResponseEntity<Map<String, Object>> buildFallback(String servicio) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "success", false,
                "message", "El servicio '" + servicio + "' no está disponible. Intente más tarde.",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
