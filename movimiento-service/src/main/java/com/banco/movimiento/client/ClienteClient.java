package com.banco.movimiento.client;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClienteClient {

    private final RestTemplate restTemplate;

    @Value("${services.cliente-service.url}")
    private String clienteServiceUrl;

    public Optional<ClienteInfo> buscarClientePorId(String clienteId) {
        try {
            String url = clienteServiceUrl + "/api/clientes/clienteId/" + clienteId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<?, ?> data = (Map<?, ?>) response.getBody().get("data");
                if (data != null) {
                    return Optional.of(new ClienteInfo(
                            String.valueOf(data.get("clienteId")),
                            String.valueOf(data.get("nombre")),
                            String.valueOf(data.get("identificacion"))
                    ));
                }
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Cliente no encontrado con clienteId: {}", clienteId);
        } catch (Exception e) {
            log.error("Error al consultar cliente-service: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con cliente-service.");
        }
        return Optional.empty();
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ClienteInfo {
        private String clienteId;
        private String nombre;
        private String identificacion;
    }
}
