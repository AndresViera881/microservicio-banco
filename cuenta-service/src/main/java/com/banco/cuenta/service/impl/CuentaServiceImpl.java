package com.banco.cuenta.service.impl;

import com.banco.cuenta.client.ClienteClient;
import com.banco.cuenta.dto.CuentaDTO;
import com.banco.cuenta.exception.BusinessException;
import com.banco.cuenta.exception.ResourceNotFoundException;
import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.repository.CuentaRepository;
import com.banco.cuenta.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteClient clienteClient;

    @Override
    public CuentaDTO crear(CuentaDTO dto) {
        if (cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta()))
            throw new BusinessException("Ya existe una cuenta con número: " + dto.getNumeroCuenta());

        ClienteClient.ClienteInfo clienteInfo = clienteClient.buscarClientePorId(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "clienteId", dto.getClienteId()));

        Cuenta cuenta = toEntity(dto);
        CuentaDTO result = toDTO(cuentaRepository.save(cuenta));
        result.setClienteNombre(clienteInfo.getNombre());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO obtenerPorId(Long id) {
        return enrich(toDTO(findById(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO obtenerPorNumeroCuenta(String numeroCuenta) {
        return enrich(toDTO(cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "numeroCuenta", numeroCuenta))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> listarTodos() {
        return cuentaRepository.findAll().stream().map(c -> enrich(toDTO(c))).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> listarPorCliente(String clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream().map(c -> enrich(toDTO(c))).collect(Collectors.toList());
    }

    @Override
    public CuentaDTO actualizar(Long id, CuentaDTO dto) {
        Cuenta cuenta = findById(id);
        if (!cuenta.getNumeroCuenta().equals(dto.getNumeroCuenta()) && cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta()))
            throw new BusinessException("Ya existe una cuenta con número: " + dto.getNumeroCuenta());

        ClienteClient.ClienteInfo clienteInfo = clienteClient.buscarClientePorId(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "clienteId", dto.getClienteId()));

        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado());
        cuenta.setClienteId(dto.getClienteId());

        CuentaDTO result = toDTO(cuentaRepository.save(cuenta));
        result.setClienteNombre(clienteInfo.getNombre());
        return result;
    }

    @Override
    public void eliminar(Long id) {
        cuentaRepository.delete(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> buscar(String termino) {
        return cuentaRepository.search(termino).stream().map(c -> enrich(toDTO(c))).collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Cuenta findById(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "id", id));
    }

    private Cuenta toEntity(CuentaDTO dto) {
        Cuenta c = new Cuenta();
        c.setNumeroCuenta(dto.getNumeroCuenta()); c.setTipoCuenta(dto.getTipoCuenta());
        c.setSaldoInicial(dto.getSaldoInicial()); c.setEstado(dto.getEstado());
        c.setClienteId(dto.getClienteId());
        return c;
    }

    public CuentaDTO toDTO(Cuenta c) {
        return CuentaDTO.builder()
                .id(c.getId()).numeroCuenta(c.getNumeroCuenta()).tipoCuenta(c.getTipoCuenta())
                .saldoInicial(c.getSaldoInicial()).estado(c.getEstado()).clienteId(c.getClienteId())
                .build();
    }

    private CuentaDTO enrich(CuentaDTO dto) {
        clienteClient.buscarClientePorId(dto.getClienteId())
                .ifPresent(info -> dto.setClienteNombre(info.getNombre()));
        return dto;
    }
}
