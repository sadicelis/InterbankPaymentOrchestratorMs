package com.payments.orchestrator.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "test_payments")
@Data
public class PaymentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String status;
    private String message;
}