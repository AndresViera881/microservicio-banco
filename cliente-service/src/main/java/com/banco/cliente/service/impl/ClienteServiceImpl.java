package com.banco.cliente.service.impl;

import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.exception.BusinessException;
import com.banco.cliente.exception.ResourceNotFoundException;
import com.banco.cliente.model.Cliente;
import com.banco.cliente.repository.ClienteRepository;
import com.banco.cliente.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    public ClienteDTO crear(ClienteDTO dto) {
        if (clienteRepository.existsByClienteId(dto.getClienteId()))
            throw new BusinessException("Ya existe un cliente con clienteId: " + dto.getClienteId());
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion()))
            throw new BusinessException("Ya existe un cliente con identificación: " + dto.getIdentificacion());
        return toDTO(clienteRepository.save(toEntity(dto)));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorId(Long id) {
        return toDTO(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorClienteId(String clienteId) {
        return toDTO(clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "clienteId", clienteId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ClienteDTO actualizar(Long id, ClienteDTO dto) {
        Cliente cliente = findById(id);
        if (!cliente.getClienteId().equals(dto.getClienteId()) && clienteRepository.existsByClienteId(dto.getClienteId()))
            throw new BusinessException("Ya existe un cliente con clienteId: " + dto.getClienteId());
        if (!cliente.getIdentificacion().equals(dto.getIdentificacion()) && clienteRepository.existsByIdentificacion(dto.getIdentificacion()))
            throw new BusinessException("Ya existe un cliente con identificación: " + dto.getIdentificacion());

        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setIdentificacion(dto.getIdentificacion());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setClienteId(dto.getClienteId());
        cliente.setContrasena(dto.getContrasena());
        cliente.setEstado(dto.getEstado());
        return toDTO(clienteRepository.save(cliente));
    }

    @Override
    public void eliminar(Long id) {
        clienteRepository.delete(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscar(String termino) {
        return clienteRepository.search(termino).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
    }

    private Cliente toEntity(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setNombre(dto.getNombre()); c.setGenero(dto.getGenero()); c.setEdad(dto.getEdad());
        c.setIdentificacion(dto.getIdentificacion()); c.setDireccion(dto.getDireccion());
        c.setTelefono(dto.getTelefono()); c.setClienteId(dto.getClienteId());
        c.setContrasena(dto.getContrasena()); c.setEstado(dto.getEstado());
        return c;
    }

    public ClienteDTO toDTO(Cliente c) {
        return ClienteDTO.builder()
                .id(c.getId()).nombre(c.getNombre()).genero(c.getGenero()).edad(c.getEdad())
                .identificacion(c.getIdentificacion()).direccion(c.getDireccion()).telefono(c.getTelefono())
                .clienteId(c.getClienteId()).contrasena(c.getContrasena()).estado(c.getEstado())
                .build();
    }
}
