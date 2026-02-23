package com.payments.orchestrator.domain.model;

import lombok.Value;

@Value
public class BankTransferResult {
    boolean success;
    String code;        // ej: "00" OK, "51" fondos insuficientes, etc.
    String message;     // mensaje para auditoría
    String externalRef; // referencia del banco (si aplica)
}