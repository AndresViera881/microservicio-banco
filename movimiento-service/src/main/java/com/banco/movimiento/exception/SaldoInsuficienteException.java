package com.banco.movimiento.exception;
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException() { super("Saldo no disponible"); }
    public SaldoInsuficienteException(String msg) { super(msg); }
}
