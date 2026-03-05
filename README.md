## Microservicios — Prueba Técnica
## Levantar con Docker

```bash
# Levantar todo (PostgreSQL + 4 microservicios)
docker compose up --build -d

# Ver logs de todos los servicios
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f api-gateway
docker compose logs -f movimiento-service
```

**Todo el tráfico entra por el Gateway en el puerto 8080:**
```
http://localhost:8080/api/clientes
http://localhost:8080/api/cuentas
http://localhost:8080/api/movimientos
http://localhost:8080/api/reportes
```

---

