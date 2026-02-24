package com.payments.orchestrator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckTest {

    private HealthCheck healthCheck = new HealthCheck();

    @Test
    void shouldReturnHealthyStatus() {
        String status = healthCheck.checkHealth();

        assertThat(status).isEqualTo("Orquestador de Pagos en línea");
    }

    @Test
    void shouldNotReturnNull() {
        String status = healthCheck.checkHealth();

        assertThat(status).isNotNull();
    }

    @Test
    void shouldReturnConsistentStatus() {
        String status1 = healthCheck.checkHealth();
        String status2 = healthCheck.checkHealth();

        assertThat(status1).isEqualTo(status2);
    }

    @Test
    void shouldIndicateApplicationIsRunning() {
        String status = healthCheck.checkHealth();

        assertThat(status).isNotBlank();
        assertThat(status).isNotEmpty();
    }
}
