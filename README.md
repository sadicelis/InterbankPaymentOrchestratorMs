# Interbank Payment Orchestrator Microservice

A **production-grade microservice** for orchestrating secure interbank fund transfers with resilience, clean architecture, and comprehensive error handling.

---

## 📋 Requirements

Before running the project, ensure you have:

- **Java 21** (or higher)
- **Gradle 9.3+** (included via `./gradlew`)
- **Docker & Docker Compose** (for SQL Server + services)
- **8GB+ RAM** (for application + Docker containers)
- **Port 8080** available (application)
- **Port 1433** available (SQL Server)

---

## 🚀 Quick Start (Local Development)

### Step 1: Clone and Prepare
```bash
cd InterbankPaymentOrchestratorMs
```

### Step 2: Start Dependencies
```bash
# Start SQL Server and required services
docker-compose up -d

# Create the database
docker exec -it sqlserver-payments /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "StrongPassword123!" -C \
  -Q "CREATE DATABASE paymentsdb;"
```

### Step 3: Run the Application
```bash
# Build and run (compiles + tests + starts app)
./gradlew bootRun
```

**That's it!** The app is now running on `http://localhost:8080`

### Step 4: Verify It's Working
```bash
# Check health status
curl http://localhost:8080/health
```
---
## 🧪 Testing

### Run All Tests
```bash
./gradlew build
# Runs: compilation + unit tests + integration tests
# Expected: **42 tests pass** in ~60 seconds
```

### Run Tests Only (Skip Compilation)
```bash
./gradlew test
```

### Test Coverage Report
```bash
./gradlew test jacocoTestReport
# Open: build/reports/jacoco/test/html/index.html
```
---

## 📚 API Documentation

### Manual API Test

**Create a Transfer** (POST):
```bash
curl -X POST http://localhost:8080/v1/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "reference": "TRANSFER_2026_001",
    "sourceBankId": "BANK_A",
    "destinationBankId": "BANK_B",
    "amount": 1500.00,
    "bankId": "BANK_1"
  }'

# Response (201 Created):
# {
#   "status": "success",
#   "data": {
#     "id": "uuid-...",
#     "paymentStatus": "PENDING",
#     "transactionId": "TRANSFER_2026_001"
#   }
# }


```
[Swagger](./openapi.yml)  |  [Collection postman test](InterbankPaymentOrchestratorMs.postman_collection.json)
---

## 📊 Technology Stack

| Component | Technology |
|-----------|-----------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.0 |
| **Architecture** | Clean Architecture + Hexagonal Pattern + DDD |
| **Reactive** | Spring WebFlux (async/non-blocking) |
| **Database** | SQL Server + JPA/Hibernate |
| **Migrations** | Flyway (automatic schema versioning) |
| **Resilience** | Resilience4j (Circuit Breaker, Retry, Timeout) |
| **Documentation** | OpenAPI/Swagger 3.0 |
| **Testing** | JUnit 5 + Mockito + Testcontainers |
| **Code Quality** | JaCoCo (code coverage) |
| **Containerization** | Docker + Kubernetes-ready manifests |

---

## 🏗️ Architecture Overview

This project follows **Clean Architecture** with **Hexagonal (Ports & Adapters)** pattern:

```
┌─────────────────────────────────────────────────┐
│          REST API Layer (HTTP)                  │
│     Controllers + Request/Response DTOs         │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│       Application Service Layer                 │
│    Business Logic Orchestration + Validation    │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│        Domain Model Layer (Core Logic)          │
│  Aggregates + Value Objects + Business Rules    │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│     Infrastructure Layer (Adapters)             │
│   Database + External APIs + Configuration      │
└─────────────────────────────────────────────────┘
```

For detailed architecture, see [ARCHITECTURE_DIAGRAMS](./ARCHITECTURE_DIAGRAMS.md).

---

## 📖 Documentation

Complete documentation is available:

| Document | Purpose |
|----------|---------|
| [ARCHITECTURE_DIAGRAMS.md](./ARCHITECTURE_DIAGRAMS.md) | System architecture with diagrams (flow, sequences, resilience patterns) |
| [DATABASE_SCHEMA.md](./DATABASE_SCHEMA.md) | Database schema with ER diagram, table definitions, indices, and SQL examples |
| [PERFORMANCE.md](./PERFORMANCE.md) | Performance testing guide using JMeter (30 TPS target, results interpretation) |
| [KUBERNETES.md](k8s/README-k8s.md)  | Production deployment manifests (7 files: namespace, config, secrets, deployment, service, account, README) |

---

## ❓ FAQ

### Q1: How do you avoid performance degradation if one bank is slow?
For these scenarios, we leverage the **Circuit Breaker Pattern** to decouple faults and maintain high availability. By integrating Resilience4j with our reactive stacks, we ensure that during high-latency spikes or 5xx errors, the circuit activates a fast fault response mechanism. This protects system resources and prevents thread exhaustion by avoiding blocking calls. This is how we work with them:

1. **Normal State (CLOSED)**: 
   - Requests to the bank go through normally
   - Example: Bank A responds in 100ms ✅

2. **During a Slowdown**:
   - Bank A starts responding in 15 seconds (> 10s timeout)
   - After **5 failures** in a row, circuit breaker **OPENS**
   - New requests receive immediate 504 Gateway Timeout instead of waiting 10s
   - This protects your app and DB pools from resource exhaustion

3. **Recovery (HALF_OPEN)**:
   - After 10 seconds, the circuit breaker tries **1 test request**
   - If it succeeds → circuit closes, normal operations resume
   - If it fails → circuit stays open, wait another 10 seconds

**Result**: Your API stays responsive even if Bank A is down or slow. Users get fast failures instead of hanging.

```
Timeline:
Time 0s:   Bank A responds in 100ms (CLOSED - normal)
Time 5s:   Bank A starts responding in 15s (still trying)
Time 7s:   5th failure - circuit OPENS (immediate timeouts)
Time 17s:  Test request sent (HALF_OPEN - probing)
Time 45s:  Bank A responsive again - circuit CLOSES (recovered)
```

### Q2: How do you ensure transactional integrity if the app crashes after confirming the transfer but before saving to the database?
To handle these scenarios, we leverage a combination of **Idempotency and Saga Pattern**. This ensures high scalability and robust transaction management by maintaining eventual consistency through compensable transactions. Our approach is defined as follows:

1. **Unique Reference Identifier**:
   - Every transfer has a unique `reference` (e.g., "TRANSFER_2026_001")
   - Database has a UNIQUE constraint on this field
   - If the same reference comes in twice, INSERT fails (duplicate detected)

2. **Two-Phase Completion**:
   - Phase 1: Send request to bank → Bank confirms transfer (bank side is done)
   - Phase 2: Save to database locally
   - If crash happens between Phase 1 & 2, the transfer **still exists** at the bank

3. **Reconciliation Process** (runs daily):
   ```sql
   -- Find transfers Bank confirms but we don't have locally
   SELECT * FROM TRANSACTIONS 
   WHERE bank_reference IS NOT NULL 
   AND local_id IS NULL;
   
   -- These need manual review or automatic correction
   ```

4. **Retry Safety**:
   - If client doesn't get response, they retry with same `reference`
   - Our idempotency check catches it: "Already processed, returning previous result"
   - No duplicate charge, no data corruption

**Result**: Even if catastrophe happens, transfers are either:
- ✅ Fully recorded (safe)
- 🔄 Partially recorded but caught by reconciliation (recoverable)
- ❌ Never recorded (detected and handled)

Never lost, never duplicated, never corrupted.

---

## 🐛 Troubleshooting

### Issue: `Connection refused` when starting app
**Cause**: SQL Server container isn't running  
**Fix**:
```bash
docker-compose up -d
docker ps  # Verify sqlserver-payments is running
```

### Issue: Tests fail with `Database not found`
**Cause**: Database migration didn't run  
**Fix**:
```bash
docker-compose restart sqlserver-payments
./gradlew build --info  # See migration logs
```

### Issue: `Port 8080 already in use`
**Cause**: Another app is using the port  
**Fix**:
```bash
# Kill process on port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux:
lsof -i :8080
kill -9 <PID>
```

### Issue: High memory usage after running tests
**Cause**: Testcontainers are still running  
**Fix**:
```bash
docker ps | grep testcontainers
docker stop <container_id>
docker system prune -a
```

---

## 🔗 Related Resources

- [Spring WebFlux Docs](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Resilience4j Circuit Breaker](https://resilience4j.readme.io/docs/getting-started-3)
- [Clean Architecture by Robert Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [JMeter Performance Testing](https://jmeter.apache.org/usermanual/)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/configuration/overview/)

---

## 📝 Version

- **Application**: v1.0.0
- **Java**: 21
- **Spring Boot**: 3.5.0
- **Last Updated**: 2026-02-23

---