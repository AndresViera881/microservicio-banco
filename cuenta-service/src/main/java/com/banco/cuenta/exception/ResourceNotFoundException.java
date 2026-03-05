package com.banco.cuenta.exception;
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String r, String f, Object v) { super(String.format("%s no encontrado con %s: '%s'",r,f,v)); }
}
