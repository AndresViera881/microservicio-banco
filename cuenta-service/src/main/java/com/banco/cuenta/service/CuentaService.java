package com.banco.cuenta.service;

import com.banco.cuenta.dto.CuentaDTO;
import java.util.List;

public interface CuentaService {
    CuentaDTO crear(CuentaDTO dto);
    CuentaDTO obtenerPorId(Long id);
    CuentaDTO obtenerPorNumeroCuenta(String numeroCuenta);
    List<CuentaDTO> listarTodos();
    List<CuentaDTO> listarPorCliente(String clienteId);
    CuentaDTO actualizar(Long id, CuentaDTO dto);
    void eliminar(Long id);
    List<CuentaDTO> buscar(String termino);
}
