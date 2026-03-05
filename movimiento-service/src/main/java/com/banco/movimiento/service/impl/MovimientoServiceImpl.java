package com.banco.movimiento.service.impl;

import com.banco.movimiento.client.CuentaClient;
import com.banco.movimiento.dto.MovimientoDTO;
import com.banco.movimiento.exception.*;
import com.banco.movimiento.model.Movimiento;
import com.banco.movimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoServiceImpl {

    private final MovimientoRepository movimientoRepository;
    private final CuentaClient cuentaClient;

    public MovimientoDTO crear(MovimientoDTO dto) {

        CuentaClient.CuentaInfo cuenta = cuentaClient
                .buscarCuentaPorNumero(dto.getNumeroCuenta())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta", "numeroCuenta", dto.getNumeroCuenta()));

        if (!cuenta.getEstado())
            throw new BusinessException(
                    "La cuenta está inactiva y no puede recibir movimientos");

        BigDecimal saldoActual =
                obtenerSaldoActual(dto.getNumeroCuenta(), cuenta.getSaldoInicial());

        BigDecimal valor = dto.getValor();

        if (valor.compareTo(BigDecimal.ZERO) < 0) {

            if (saldoActual.compareTo(BigDecimal.ZERO) == 0)
                throw new SaldoInsuficienteException();

            if (saldoActual.add(valor).compareTo(BigDecimal.ZERO) < 0)
                throw new SaldoInsuficienteException(
                        "Saldo no disponible. Saldo actual: " + saldoActual);
        }

        Movimiento mov = new Movimiento();

        mov.setFecha(LocalDateTime.now());
        mov.setTipoMovimiento(valor.compareTo(BigDecimal.ZERO) >= 0 ? "CREDITO" : "DEBITO");
        mov.setValor(valor);
        mov.setSaldo(saldoActual.add(valor));
        mov.setNumeroCuenta(dto.getNumeroCuenta());

        MovimientoDTO result = toDTO(movimientoRepository.save(mov));
        result.setClienteNombre(cuenta.getClienteNombre());

        return result;
    }

    @Transactional(readOnly = true)
    public MovimientoDTO obtenerPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> listarTodos() {
        return movimientoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> listarPorCuenta(String numeroCuenta) {
        return movimientoRepository.findByNumeroCuentaOrderByFechaDesc(numeroCuenta)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MovimientoDTO actualizar(Long id, MovimientoDTO dto) {
        Movimiento mov = findById(id);
        mov.setFecha(dto.getFecha() != null ? dto.getFecha() : mov.getFecha());
        if (dto.getValor() != null) {
            mov.setValor(dto.getValor());
            mov.setTipoMovimiento(dto.getValor().compareTo(BigDecimal.ZERO) >= 0 ? "CREDITO" : "DEBITO");
        }
        return toDTO(movimientoRepository.save(mov));
    }

    public void eliminar(Long id) {
        movimientoRepository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> buscar(String termino) {
        return movimientoRepository.search(termino).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BigDecimal obtenerSaldoActual(String numeroCuenta, BigDecimal saldoInicial) {
        return movimientoRepository.findLastByNumeroCuenta(numeroCuenta)
                .map(Movimiento::getSaldo)
                .orElse(saldoInicial);
    }

    private Movimiento findById(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento", "id", id));
    }

    public MovimientoDTO toDTO(Movimiento m) {
        return MovimientoDTO.builder()
                .id(m.getId()).fecha(m.getFecha()).tipoMovimiento(m.getTipoMovimiento())
                .valor(m.getValor()).saldo(m.getSaldo()).numeroCuenta(m.getNumeroCuenta())
                .build();
    }
}
