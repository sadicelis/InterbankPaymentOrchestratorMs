package com.payments.orchestrator.infrastructure.adapter.out.persistence.adapter;

import com.payments.orchestrator.application.exception.BankNotFoundException;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.TransferRepositoryPort;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.TransactionRepository;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.BankJpaRepository;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.TransactionEntity;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.mapper.TransactionMapper;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.BankEntity;
import com.payments.orchestrator.infrastructure.exception.PersistenceException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransferRepositoryPort {

    private final TransactionRepository repository;
    private final BankJpaRepository bankRepository;
    private final TransactionMapper mapper;

    public Mono<Transaction> savePending(Transaction request) {

        return Mono.fromCallable(() -> {

            BankEntity bank = bankRepository
                    .findByCodeAndActiveTrue(request.getBankId())
                    .orElseThrow(() -> new BankNotFoundException(
                            "Active bank not found with code: " + request.getBankId()
                    ));

            TransactionEntity entity = mapper.toEntity(request, bank);

            return mapper.toDomain(repository.save(entity));

        }).subscribeOn(Schedulers.boundedElastic())
          .onErrorMap(ex -> {
              if (ex instanceof BankNotFoundException) {
                  return ex;
              }
              return new PersistenceException("Error saving pending transaction: " + ex.getMessage(), ex);
          });
    }

    public Mono<Transaction> updateStatus(Transaction transaction) {
        return Mono.fromCallable(() -> {
            TransactionEntity entity = repository.findById(transaction.getId())
                    .orElseThrow(() -> new PersistenceException(
                            "Transaction not found: " + transaction.getId()
                    ));
            TransactionEntity mapped = mapper.toEntity(transaction, entity.getBank());
            mapped.setCreatedAt(entity.getCreatedAt());
            return mapper.toDomain(repository.save(mapped));
        }).subscribeOn(Schedulers.boundedElastic())
          .onErrorMap(ex -> {
              if (ex instanceof PersistenceException) {
                  return ex;
              }
              return new PersistenceException("Error updating transaction status: " + ex.getMessage(), ex);
          });
    }

}