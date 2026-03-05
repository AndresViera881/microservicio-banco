package com.banco.cuenta.controller;

import com.banco.cuenta.dto.ApiResponse;
import com.banco.cuenta.dto.CuentaDTO;
import com.banco.cuenta.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaDTO>> crear(@Valid @RequestBody CuentaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cuenta creada exitosamente", cuentaService.crear(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CuentaDTO>>> listar(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String clienteId) {
        List<CuentaDTO> lista;
        if (search != null && !search.isBlank()) lista = cuentaService.buscar(search);
        else if (clienteId != null && !clienteId.isBlank()) lista = cuentaService.listarPorCliente(clienteId);
        else lista = cuentaService.listarTodos();
        return ResponseEntity.ok(ApiResponse.success(lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cuentaService.obtenerPorId(id)));
    }

    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<ApiResponse<CuentaDTO>> obtenerPorNumero(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(ApiResponse.success(cuentaService.obtenerPorNumeroCuenta(numeroCuenta)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody CuentaDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Cuenta actualizada", cuentaService.actualizar(id, dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> patch(@PathVariable Long id, @RequestBody CuentaDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Cuenta actualizada", cuentaService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        cuentaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Cuenta eliminada", null));
    }
}
