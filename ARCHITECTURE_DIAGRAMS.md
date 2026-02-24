# Architecture Diagrams - Interbank Payment Orchestrator

---

## 📊 Solution Flowchart

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
