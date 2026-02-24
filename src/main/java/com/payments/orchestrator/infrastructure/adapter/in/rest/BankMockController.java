package com.payments.orchestrator.infrastructure.adapter.in.rest;

import com.payments.orchestrator.infrastructure.config.constants.ApiPathConstants;
import com.payments.orchestrator.infrastructure.config.constants.ResponseStatusConstants;
import com.payments.orchestrator.infrastructure.config.constants.DefaultValuesConstants;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping(ApiPathConstants.MOCK_BANK_BASE)
public class BankMockController {

        @PostMapping(ApiPathConstants.BANK_TRANSFER_PATH)
        public Mono<Map<String, String>> simulateTransfer(@RequestBody Map<String, Object> request) {
                return Mono.just(Map.of(
                                "status", ResponseStatusConstants.BANK_STATUS_SUCCESS,
                                "transactionId", java.util.UUID.randomUUID().toString(),
                                "message", DefaultValuesConstants.MSG_TRANSFER_PROCESSED));
        }
}