package com.payments.orchestrator.infrastructure.config.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Error Constants Tests")
class ErrorConstantsTest {

    @Test
    @DisplayName("ErrorCodesConstants debe contener códigos de error definidos")
    void testErrorCodesConstantsAreDefined() {
        assertThat(ErrorCodesConstants.ERR_INVALID_TRANSACTION).isNotNull();
        assertThat(ErrorCodesConstants.ERR_BANK_NOT_FOUND).isNotNull();
        assertThat(ErrorCodesConstants.ERR_TIMEOUT).isNotNull();
        assertThat(ErrorCodesConstants.ERR_INTERNAL_SERVER_ERROR).isNotNull();
    }

    @Test
    @DisplayName("ErrorMessagesConstants debe contener mensajes de error")
    void testErrorMessagesConstantsAreDefined() {
        assertThat(ErrorMessagesConstants.MSG_INVALID_TRANSACTION).isNotNull();
        assertThat(ErrorMessagesConstants.MSG_BANK_NOT_FOUND).isNotNull();
        assertThat(ErrorMessagesConstants.MSG_TIMEOUT).isNotNull();
        assertThat(ErrorMessagesConstants.MSG_INTERNAL_ERROR).isNotNull();
    }

    @Test
    @DisplayName("Códigos de error deben ser consistentes")
    void testErrorCodesConsistency() {
        assertThat(ErrorCodesConstants.ERR_INVALID_TRANSACTION)
                .startsWith("ERR_");
        assertThat(ErrorCodesConstants.ERR_INSUFFICIENT_FUNDS)
                .startsWith("ERR_");
        assertThat(ErrorCodesConstants.ERR_PERSISTENCE)
                .startsWith("ERR_");
    }

    @Test
    @DisplayName("SUCCESS code debe tener valor esperado")
    void testSuccessCode() {
        assertThat(ErrorCodesConstants.SUCCESS).isEqualTo("SUCCESS");
    }
}
