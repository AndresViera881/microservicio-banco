package com.banco.movimiento.client;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class CuentaClient {

    private final RestTemplate restTemplate;

    @Value("${services.cuenta-service.url}")
    private String cuentaServiceUrl;

    public Optional<CuentaInfo> buscarCuentaPorNumero(String numeroCuenta) {

        try {

            String url = cuentaServiceUrl + "/api/cuentas/numero/" + numeroCuenta;

            ResponseEntity<Map> response =
                    restTemplate.getForEntity(url, Map.class);

            if(response.getBody() == null)
                return Optional.empty();

            Map<String,Object> data =
                    (Map<String,Object>) response.getBody().get("data");

            if(data == null)
                return Optional.empty();

            CuentaInfo cuenta = CuentaInfo.builder()
                    .id(((Number)data.get("id")).longValue())
                    .numeroCuenta((String)data.get("numeroCuenta"))
                    .tipoCuenta((String)data.get("tipoCuenta"))
                    .saldoInicial(new BigDecimal(data.get("saldoInicial").toString()))
                    .estado((Boolean)data.get("estado"))
                    .clienteId((String)data.get("clienteId"))
                    .clienteNombre((String)data.get("clienteNombre"))
                    .build();

            return Optional.of(cuenta);

        } catch (HttpClientErrorException.NotFound e) {

            log.warn("Cuenta no encontrada: {}", numeroCuenta);
            return Optional.empty();

        } catch (Exception e) {

            log.error("Error consultando cuenta: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<CuentaInfo> obtenerCuentasDelCliente(String clienteId) {

        try {

            String url = cuentaServiceUrl + "/api/cuentas?clienteId=" + clienteId;

            ResponseEntity<Map> response =
                    restTemplate.getForEntity(url, Map.class);

            if (response.getBody() == null)
                return Collections.emptyList();

            List<Map<String, Object>> cuentas =
                    (List<Map<String, Object>>) response.getBody().get("data");

            if (cuentas == null)
                return Collections.emptyList();

            return cuentas.stream().map(data ->
                    CuentaInfo.builder()
                            .id(((Number) data.get("id")).longValue())
                            .numeroCuenta((String) data.get("numeroCuenta"))
                            .tipoCuenta((String) data.get("tipoCuenta"))
                            .saldoInicial(new BigDecimal(data.get("saldoInicial").toString()))
                            .estado((Boolean) data.get("estado"))
                            .clienteId((String) data.get("clienteId"))
                            .clienteNombre((String) data.get("clienteNombre"))
                            .build()
            ).toList();

        } catch (Exception e) {

            log.error("Error consultando cuentas: {}", e.getMessage());

            return Collections.emptyList();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuentaInfo {

        private Long id;
        private String numeroCuenta;
        private String tipoCuenta;
        private BigDecimal saldoInicial;
        private Boolean estado;
        private String clienteId;
        private String clienteNombre;

    }
}
