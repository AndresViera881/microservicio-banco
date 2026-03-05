package com.banco.cliente.service;

import com.banco.cliente.dto.ClienteDTO;
import java.util.List;

public interface ClienteService {
    ClienteDTO crear(ClienteDTO dto);
    ClienteDTO obtenerPorId(Long id);
    ClienteDTO obtenerPorClienteId(String clienteId);
    List<ClienteDTO> listarTodos();
    ClienteDTO actualizar(Long id, ClienteDTO dto);
    void eliminar(Long id);
    List<ClienteDTO> buscar(String termino);
}
