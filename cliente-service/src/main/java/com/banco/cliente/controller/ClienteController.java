package com.banco.cliente.controller;

import com.banco.cliente.dto.ApiResponse;
import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteDTO>> crear(@Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente creado exitosamente", clienteService.crear(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> listar(
            @RequestParam(required = false) String search) {
        List<ClienteDTO> lista = (search != null && !search.isBlank())
                ? clienteService.buscar(search) : clienteService.listarTodos();
        return ResponseEntity.ok(ApiResponse.success(lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.obtenerPorId(id)));
    }

    @GetMapping("/clienteId/{clienteId}")
    public ResponseEntity<ApiResponse<ClienteDTO>> obtenerPorClienteId(@PathVariable String clienteId) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.obtenerPorClienteId(clienteId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Cliente actualizado", clienteService.actualizar(id, dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> patch(
            @PathVariable Long id, @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Cliente actualizado", clienteService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Cliente eliminado", null));
    }
}
