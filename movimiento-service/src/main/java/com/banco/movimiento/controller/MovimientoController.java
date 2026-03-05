package com.banco.movimiento.controller;

import com.banco.movimiento.dto.ApiResponse;
import com.banco.movimiento.dto.MovimientoDTO;
import com.banco.movimiento.dto.ReporteDTO;
import com.banco.movimiento.service.impl.MovimientoServiceImpl;
import com.banco.movimiento.service.impl.ReporteServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoServiceImpl movimientoService;
    private final ReporteServiceImpl reporteService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoDTO>> crear(@Valid @RequestBody MovimientoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movimiento registrado exitosamente", movimientoService.crear(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimientoDTO>>> listar(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String numeroCuenta) {
        List<MovimientoDTO> lista;
        if (search != null && !search.isBlank()) lista = movimientoService.buscar(search);
        else if (numeroCuenta != null && !numeroCuenta.isBlank()) lista = movimientoService.listarPorCuenta(numeroCuenta);
        else lista = movimientoService.listarTodos();
        return ResponseEntity.ok(ApiResponse.success(lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(movimientoService.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody MovimientoDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Movimiento actualizado", movimientoService.actualizar(id, dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoDTO>> patch(@PathVariable Long id, @RequestBody MovimientoDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Movimiento actualizado", movimientoService.actualizar(id, dto)));
    }
}
