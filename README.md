# 🏦 Banco Microservicios — Prueba Técnica

Arquitectura de **microservicios reales** con Java 21 + Spring Boot 3.2 + Spring Cloud Gateway.

---

## 🏗️ Arquitectura

```
                    ┌─────────────────────────────────┐
  Cliente HTTP  ──► │   API Gateway  (puerto 8080)     │
                    │   Spring Cloud Gateway           │
                    │   • Enrutamiento                 │
                    │   • CORS global                  │
                    │   • Circuit Breaker              │
                    │   • Logging de requests          │
                    └──────────────┬──────────────────┘
                                   │ enruta a:
              ┌────────────────────┼────────────────────┐
              │                    │                    │
              ▼                    ▼                    ▼
   ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
   │ cliente-service  │ │  cuenta-service  │ │movimiento-service│
   │   (port 8081)    │◄│   (port 8082)    │◄│   (port 8083)    │
   │                  │ │                  │ │  + reportes      │
   │  schema:         │ │  schema:         │ │  schema:         │
   │  cliente_schema  │ │  cuenta_schema   │ │  movimiento_     │
   └──────────────────┘ └──────────────────┘ │  schema          │
              │                              └──────────────────┘
              └──────────────────────────────────────────┘
                              PostgreSQL 16
```

### Comunicación inter-servicios
- **cuenta-service** → llama a **cliente-service** via REST para validar que el cliente existe
- **movimiento-service** → llama a **cuenta-service** via REST para validar cuenta y obtener saldo inicial
- **movimiento-service** → llama a **cliente-service** via REST para generar reportes
- Cada servicio tiene su propio schema en PostgreSQL (**independencia de datos**)

---

## 🚀 Levantar con Docker

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

## 📡 Endpoints (todos accesibles via el Gateway)

| Método | Endpoint                      | Servicio destino     |
|--------|-------------------------------|----------------------|
| POST   | `/api/clientes`               | cliente-service      |
| GET    | `/api/clientes`               | cliente-service      |
| GET    | `/api/clientes/{id}`          | cliente-service      |
| PUT    | `/api/clientes/{id}`          | cliente-service      |
| DELETE | `/api/clientes/{id}`          | cliente-service      |
| POST   | `/api/cuentas`                | cuenta-service       |
| GET    | `/api/cuentas`                | cuenta-service       |
| GET    | `/api/cuentas/{id}`           | cuenta-service       |
| PUT    | `/api/cuentas/{id}`           | cuenta-service       |
| DELETE | `/api/cuentas/{id}`           | cuenta-service       |
| POST   | `/api/movimientos`            | movimiento-service   |
| GET    | `/api/movimientos`            | movimiento-service   |
| DELETE | `/api/movimientos/{id}`       | movimiento-service   |
| GET    | `/api/reportes?clienteId=X&fechaInicio=Y&fechaFin=Z` | movimiento-service |

---

## 💼 Reglas de negocio

- Créditos = valor **positivo**, débitos = valor **negativo**
- Si saldo = 0 y se intenta un débito → `"Saldo no disponible"` (HTTP 400)
- El saldo disponible se calcula sobre el último movimiento de la cuenta
- El reporte retorna JSON + PDF en Base64

---

## 🧪 Tests unitarios

```bash
# Ejecutar tests de cada servicio
cd cliente-service   && mvn test
cd movimiento-service && mvn test
```

---

## 📬 Postman

Importar `BancoMicroservicios.postman_collection.json`. Todas las peticiones apuntan al Gateway `http://localhost:8080/api`.

---

## 🗄️ Schemas de base de datos

| Schema               | Servicio             |
|----------------------|----------------------|
| `cliente_schema`     | cliente-service      |
| `cuenta_schema`      | cuenta-service       |
| `movimiento_schema`  | movimiento-service   |
