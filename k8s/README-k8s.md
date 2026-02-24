# Kubernetes Manifests for Interbank Payment Orchestrator

## Description

This directory contains the Kubernetes manifests to deploy the **Interbank Payment Orchestrator** microservice in a Kubernetes cluster.

## File Structure

- `namespace.yaml` - Namespace to isolate orchestrator resources
- `configmap.yaml` - Application configuration (profiles, logging, resilience)
- `secret.yaml` - Database secrets and credentials
- `deployment.yaml` - Deployment with replicas, health checks, resource limits
- `service.yaml` - ClusterIP Service to expose the application internally
- `serviceaccount.yaml` - ServiceAccount for RBAC

## Deployment

### Prerequisites

- Kubernetes 1.20+
- A Docker image of the orchestrator in a local or remote registry
  ```bash
  docker build -t payment-orchestrator:latest .
  ```

### Installation

1. **Create the namespace:**
   ```bash
   kubectl apply -f namespace.yaml
   ```

2. **Apply ConfigMap and Secrets:**
   ```bash
   kubectl apply -f configmap.yaml
   kubectl apply -f secret.yaml
   ```

   > ⚠️ **IMPORTANT**: Update `secret.yaml` with real credentials before deploying to production.

3. **Create the ServiceAccount:**
   ```bash
   kubectl apply -f serviceaccount.yaml
   ```

4. **Deploy the application:**
   ```bash
   kubectl apply -f deployment.yaml
   kubectl apply -f service.yaml
   ```

5. **Verify the deployment:**
   ```bash
   # View pods
   kubectl get pods -n payment-orchestrator

   # View services
   kubectl get svc -n payment-orchestrator

   # View logs
   kubectl logs -n payment-orchestrator deployment/orchestrator

   # Describe deployment
   kubectl describe deployment orchestrator -n payment-orchestrator
   ```

### Deploy Everything at Once

```bash
kubectl apply -f k8s/
```

## Detailed Configuration

### Deployment

- **Replicas**: 2 (can be scaled with `kubectl scale`)
- **Strategy**: RollingUpdate with maxSurge=1, maxUnavailable=0
- **Liveness Probe**: Checks `/health` every 10s (failover after 3 attempts)
- **Readiness Probe**: Checks `/health` every 5s (failover after 2 attempts)
- **Resource Requests**: 250m CPU, 512Mi RAM
- **Resource Limits**: 500m CPU, 1Gi RAM
- **Security**: runAsNonRoot, readOnlyRootFilesystem, no privileges
- **Anti-affinity**: Tries to spread pods across different nodes

### Service

- **Type**: ClusterIP (only accessible from within the cluster)
- **Port**: 8080
- **Selects pods** with label `app: orchestrator`

### ConfigMap

- Spring profiles configuration (prod)
- Logging levels
- External bank URLs
- Resilience4j parameters (Circuit Breaker, Retry)

### Secret

- SQL Server credentials
- Bank API keys (placeholders)
- Application secret key

## Scaling

To scale manually:

```bash
kubectl scale deployment orchestrator --replicas=3 -n payment-orchestrator
```

For auto-scaling (requires HPA):

```bash
kubectl autoscale deployment orchestrator --min=2 --max=5 --cpu-percent=80 -n payment-orchestrator
```

## Monitoring

Check pod status:

```bash
kubectl get pods -n payment-orchestrator -w
```

View events:

```bash
kubectl get events -n payment-orchestrator
```

Stream logs:

```bash
kubectl logs -f deployment/orchestrator -n payment-orchestrator
```

## Cleanup

To delete all resources:

```bash
kubectl delete namespace payment-orchestrator
```

## Important Notes

1. **Database**: Ensure SQL Server is available at the configured address
2. **Banking services**: Bank endpoints must be reachable from the cluster
3. **Secrets**: Replace `secret.yaml` with real credentials before production
4. **Images**: Replace `image: "payment-orchestrator:latest"` with the real registry image
5. **Certificates**: In production, use TLS/HTTPS with cert-manager
6. **RBAC**: The ServiceAccount has minimal permissions. Expand as needed with RoleBindings
