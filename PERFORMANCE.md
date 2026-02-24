# Performance Testing Guide - Interbank Payment Orchestrator

## 🎯 What We're Testing

This document outlines the strategy to validate that the component meets the required 30 TPS (Transactions Per Second) under sustained load conditions.

---
## 🛠️ Tooling & Infrastructure
   - JMeter: Selected for distributed load generation and concurrent user simulation.

   - CloudWatch /Grafana: Used for real-time resource monitoring (CPU, Memory, Network I/O).

   - AWS X-Ray (Optional): To identify bottlenecks through distributed tracing.

---
## 📊 Target Metrics

| Metric | Target | What It Means |
|--------|--------|---------------|
| **Throughput** | ≥ 30 TPS | Process 30 requests/sec |
| **Latency P50** | < 100ms | 50% of requests finish in this time |
| **Latency P99** | < 500ms | 99% of requests finish in this time (critical) |
| **Error Rate** | < 0.5% | Fewer than 5 errors per 1000 requests |
| **CPU Usage** | < 70% | Not exhausting server power |
| **Memory Usage** | < 80% | Not running out of memory |

---
## 🧪 Testing Plan

We will execute a four-stage plan to understand the component's elastic behavior and stability:

### Phase 1: Baseline (No Load)
- **Load**: 1 user, 1 request
- **Purpose**: Know the minimum latency
- **Result**: Should see ~50-100ms for a single request

### Phase 2: Ramp Up (Gradual Load)
- **Load**: 10 → 20 → 30 → 40 users (TPS)
- **Purpose**: See where latency starts increasing
- **Result**: Latency should increase gradually

### Phase 3: Target Load (30 TPS - 10 minutes)
- **Load**: Sustained 30 users
- **Purpose**: Confirm we can maintain target consistently
- **Pass Criteria**: P99 < 500ms, Error Rate < 0.5%

### Phase 4: Stress (Finding the Limit)
- **Load**: 40 → 50 → 60 users
- **Purpose**: Find breaking point
- **Result**: Where does it fail?

---
## 🎓 Why These Numbers?

**30 TPS because**:
- Typical bank processing: 20-50 TPS per institution
- Our system handles small-to-medium interbank volume
- Room for growth to 100+ TPS with Kubernetes scaling

**P99 < 500ms because**:
- P50 is average user experience (< 100ms feels instant)
- P99 is worst-case experience (5 in 1000 users see this)
- 500ms is still acceptable for financial transactions
- Anything above 1 second feels broken to users

**< 0.5% errors because**:
- Financial transactions can't fail often
- 1% error = 300,000 failed transactions/day
- 0.5% = 150,000 failed/day (still significant, but manageable)

---
## 💡 Engineering Insights
   - Environment Isolation: Tests will be conducted in a Mirror or Staging environment to avoid impacting production traffic.

   - JVM Warm-up: A pre-heating phase will be performed to ensure the JIT compiler has optimized the bytecode before formal measurement.

   - Dynamic Data Sets: Use of varied test data to bypass cache hits and simulate realistic input variety.