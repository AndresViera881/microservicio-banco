package com.banco.cuenta.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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

    /**
     * Consulta el cliente-service para verificar que existe un cliente con ese clienteId.
     * Retorna el nombre del cliente si existe, vacío si no.
     */
    public Optional<ClienteInfo> buscarClientePorId(String clienteId) {
        try {
            String url = clienteServiceUrl + "/api/clientes/clienteId/" + clienteId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<?, ?> data = (Map<?, ?>) response.getBody().get("data");
                if (data != null) {
                    return Optional.of(new ClienteInfo(
                            String.valueOf(data.get("clienteId")),
                            String.valueOf(data.get("nombre"))
                    ));
                }
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Cliente no encontrado con clienteId: {}", clienteId);
        } catch (Exception e) {
            log.error("Error al consultar cliente-service: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con cliente-service. " +
                    "Verifique que el servicio esté activo.");
        }
        return Optional.empty();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteInfo {
        private String clienteId;
        private String nombre;
    }
}
