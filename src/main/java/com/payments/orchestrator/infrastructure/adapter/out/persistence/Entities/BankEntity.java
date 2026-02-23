package com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}