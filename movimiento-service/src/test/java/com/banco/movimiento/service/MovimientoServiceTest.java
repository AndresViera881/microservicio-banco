package com.banco.movimiento.service;

import com.banco.movimiento.client.CuentaClient;
import com.banco.movimiento.dto.MovimientoDTO;
import com.banco.movimiento.exception.ResourceNotFoundException;
import com.banco.movimiento.exception.SaldoInsuficienteException;
import com.banco.movimiento.model.Movimiento;
import com.banco.movimiento.repository.MovimientoRepository;
import com.banco.movimiento.service.impl.MovimientoServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoService — Tests Unitarios")
class MovimientoServiceTest {

    @Mock MovimientoRepository movimientoRepository;
    @Mock CuentaClient cuentaClient;
    @InjectMocks MovimientoServiceImpl movimientoService;

    private CuentaClient.CuentaInfo cuentaActiva;

    @BeforeEach
    void setUp() {
        cuentaActiva = CuentaClient.CuentaInfo.builder()
                .id(1L).numeroCuenta("478758").tipoCuenta("Ahorro")
                .saldoInicial(new BigDecimal("2000.00")).estado(true)
                .clienteId("CL001").clienteNombre("Jose Lema").build();
    }

    @Test
    @DisplayName("Debe registrar crédito y calcular saldo = saldoInicial + valor")
    void debeRegistrarCredito() {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setNumeroCuenta("478758");
        dto.setValor(new BigDecimal("600.00"));
        dto.setTipoMovimiento("CREDITO");

        when(cuentaClient.buscarCuentaPorNumero("478758")).thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.findLastByNumeroCuenta("478758")).thenReturn(Optional.empty());

        Movimiento saved = new Movimiento();
        saved.setId(1L); saved.setTipoMovimiento("CREDITO");
        saved.setValor(new BigDecimal("600.00")); saved.setSaldo(new BigDecimal("2600.00"));
        saved.setNumeroCuenta("478758");
        when(movimientoRepository.save(any())).thenReturn(saved);

        MovimientoDTO result = movimientoService.crear(dto);

        assertThat(result.getTipoMovimiento()).isEqualTo("CREDITO");
        assertThat(result.getSaldo()).isEqualByComparingTo("2600.00");
        verify(movimientoRepository).save(any());
    }

    @Test
    @DisplayName("Debe lanzar SaldoInsuficienteException cuando saldo es 0 y débito")
    void debeLanzarSaldoInsuficiente_CuandoSaldoCero() {
        cuentaActiva.setSaldoInicial(BigDecimal.ZERO);

        MovimientoDTO dto = new MovimientoDTO();
        dto.setNumeroCuenta("478758"); dto.setValor(new BigDecimal("-100.00"));

        when(cuentaClient.buscarCuentaPorNumero("478758")).thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.findLastByNumeroCuenta("478758")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.crear(dto))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessageContaining("Saldo no disponible");

        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar SaldoInsuficienteException cuando débito supera saldo disponible")
    void debeLanzarSaldoInsuficiente_CuandoDebitoSuperaSaldo() {
        cuentaActiva.setSaldoInicial(new BigDecimal("100.00"));

        MovimientoDTO dto = new MovimientoDTO();
        dto.setNumeroCuenta("478758"); dto.setValor(new BigDecimal("-500.00"));

        when(cuentaClient.buscarCuentaPorNumero("478758")).thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.findLastByNumeroCuenta("478758")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.crear(dto))
                .isInstanceOf(SaldoInsuficienteException.class);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando cuenta no existe")
    void debeLanzarExcepcion_CuentaNoExiste() {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setNumeroCuenta("999999"); dto.setValor(new BigDecimal("100.00"));

        when(cuentaClient.buscarCuentaPorNumero("999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.crear(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Debe tomar el saldo del último movimiento, no del saldoInicial")
    void debeUsarSaldoUltimoMovimiento() {
        Movimiento ultimo = new Movimiento();
        ultimo.setSaldo(new BigDecimal("500.00"));
        ultimo.setNumeroCuenta("478758");

        MovimientoDTO dto = new MovimientoDTO();
        dto.setNumeroCuenta("478758"); dto.setValor(new BigDecimal("-200.00"));

        when(cuentaClient.buscarCuentaPorNumero("478758")).thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.findLastByNumeroCuenta("478758")).thenReturn(Optional.of(ultimo));

        Movimiento saved = new Movimiento();
        saved.setId(2L); saved.setTipoMovimiento("DEBITO");
        saved.setValor(new BigDecimal("-200.00")); saved.setSaldo(new BigDecimal("300.00"));
        saved.setNumeroCuenta("478758");
        when(movimientoRepository.save(any())).thenReturn(saved);

        MovimientoDTO result = movimientoService.crear(dto);

        assertThat(result.getSaldo()).isEqualByComparingTo("300.00");
        assertThat(result.getTipoMovimiento()).isEqualTo("DEBITO");
    }
}
