# Architecture Diagrams - Interbank Payment Orchestrator

---

## 📊 Solution flowchart

![alt text](<src/main/resources/images/Solution flowchart.png>)

---

## 🔄 Sequence Diagram - Success Story

![alt text](<src/main/resources/images/Sequence Diagram - Success Story.png>)

---

## ⚠️ Sequence Diagram - Error Case (Slow Bank)

![alt text](<src/main/resources/images/Sequence Diagram - Error Case (Slow Bank).png>)

---

## 🏛️ Layered Architecture

![alt text](<src/main/resources/images/Layered Architecture.png>)

---

## 🔌 Hexagonal Pattern - Ports and Adapters

```
┌─────────────────────────────────────────────────────────────┐
│                    External World                           │
│  [Client App]        [Bank APIs]        [Messaging Queue]   │
└────────────┬──────────────┬────────────────┬────────────────┘
             │              │                │
         ┌───┴──────┬───────┴────────┬───────┴─────┐
         │ Adapter  │ Adapter        │ Adapter     │
         ├──────────┼────────────────┼─────────────┤
    
    ╔═════════════════════════════════════════════════════════╗
    ║              HEXAGON (Core Application)                 ║
    ║                                                         ║
    ║  ┌──────────────────────────────────────────────────┐   ║
    ║  │  Application Service (Transfer Orchestration)    │   ║
    ║  └──────────────────────────────────────────────────┘   ║
    ║           │                              │              ║
    ║           ▼                              ▼              ║
    ║  ┌─────────────────┐        ┌─────────────────────┐     ║
    ║  │ Domain Layer    │        │ Domain Layer        │     ║
    ║  │ (Aggregates &   │        │ (Ports - Interfaces)│     ║
    ║  │  Business Rules)│        │                     │     ║
    ║  └─────────────────┘        └─────────────────────┘     ║
    ║           △                             △              ║
    ║           │                              │              ║
    ║  ┌────────┴──────────────────────────────┴────────┐     ║
    ║  │  Domain Events, Specifications                 │     ║
    ║  └────────────────────────────────────────────────┘     ║
    ║                                                         ║
    ╚═════════════════════════════════════════════════════════╝
             │              │                │
         ┌───┴──────┬───────┴────────┬───────┴─────┐
         │ Adapter  │ Adapter        │ Adapter     │
         │ REST IN  │ PERSISTENCE    │ BANK OUT    │
         └───┬──────┴────────────────┴─────────────┘
             │
         [Database]  [External Bank APIs]  [HTTP]
```
---

## 🔄 Error Management - Circuit Breaker

```
                    Request
                       │
         ┌─────────────▼──────────────┐
         │   Circuit Breaker State?   │
         └─────────┬──────────┬───────┘
                   │          │
            ┌──────▼──┐   ┌───▼──────┐
            │  OPEN   │   │ CLOSED   │
            └────┬────┘   │          │
                 │        └───┬──────┘
         ┌───────┤            │
         │       │            ▼
    ┌────▼───┐   │    ┌──────────────┐
    │ 503    │   │    │ Try Request  │
    │Service │   │    │              │
    │Unavail │   │    └───┬────┬─────┘
    └────────┘   │        │    │
                 │   ┌────▼─ ──▼────┐
                 │   │              │
                 │ ┌─▼──┐      ┌────▼─┐
                 │ │ OK │      │ FAIL │
                 │ └────┘      └─┬──┬─┘
                 │               │  │
                 │          ┌────▼──▼──────┐
                 │          │ Count++      │
                 │          │ If >= 50%    │
                 │          │ → OPEN (Wait)│
                 │          └──────────────┘
                 │
          ┌──────┴─────────┐
          │                │
    ┌─────▼──┐      ┌──────▼──────┐
    │ Return │      │After 10s    │
    │ Error  │      │HALF_OPEN    │
    │ 503    │      │Try request  │
    └────────┘      └─────────────┘
```

---

## 📊 Transaction Status

![alt text](<src/main/resources/images/Transaction Status.png>)

---

## 🛡️ Resilience Complexity

```
Request → [Timeout: 10s]
           ↓
        [Retry: 3 attempts]
           ↓
        [Circuit Breaker]
           │
           ├─ CLOSED: Accept request
           ├─ OPEN: Fast fail (< 100ms) with 503
           └─ HALF_OPEN: Test recovery
           ↓
    [External Bank API]
           ├─ Success → Update BD → 201
           ├─ Timeout → TimeoutException → 504
           ├─ Connection Error → BankCommunicationException → 503
           └─ Invalid Response → ExternalServiceException → 503
           ↓
    [GlobalExceptionHandler]
           ├─ Maps exception to HTTP status
           ├─ Formats ApiResponse
           └─ Returns to client
```

---

## 📈 Data Flow - Request to Response

```
HTTP Request (JSON)
    │
    ├─► Deserialize → TransferRequestDto
    │
    ├─► Validate @Valid
    │   └─► ConstraintViolationException → 400
    │
    ├─► Create Domain → Transaction.create()
    │   └─► InvalidTransactionException → 400
    │
    ├─► Save PENDING → BD
    │   └─► PersistenceException → 500
    │
    ├─► Call Bank
    │   └─► BankCommunicationException → 503
    │
    ├─► Update Status → COMPLETED
    │
    └─► Serialize & Return
        ├─ Success: 201 Created + ApiResponse<TransferResponseDto>
        ├─ Error: 4xx/5xx + ApiResponse<ErrorDetails>
        └─ JSON Response

ApiResponse wrapper:
{
  "status": "SUCCESS|ERROR",
  "code": "OK|ERR_INVALID_TRANSACTION|...",
  "message": "User-friendly message",
  "data": {...} or null,
  "error": {...} or null,
  "timestamp": "ISO-8601",
  "correlationId": "UUID"
}
```

