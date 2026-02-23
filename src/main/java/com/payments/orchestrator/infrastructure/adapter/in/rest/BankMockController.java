package com.payments.orchestrator.infrastructure.adapter.in.rest;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/mock/bank")
public class BankMockController {

        @PostMapping("/transfer")
        public Mono<Map<String, String>> simulateTransfer(@RequestBody Map<String, Object> request) {
                return Mono.just(Map.of(
                                "status", "SUCCESS",
                                "transactionId", java.util.UUID.randomUUID().toString(),
                                "message", "Transfer processed by mock bank"));
        }
}