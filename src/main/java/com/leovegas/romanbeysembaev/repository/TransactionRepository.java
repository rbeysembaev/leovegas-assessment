package com.leovegas.romanbeysembaev.repository;

import com.leovegas.romanbeysembaev.entity.Transaction;
import com.leovegas.romanbeysembaev.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByExternalId(UUID externalId);
    List<Transaction> findAllByWalletOrderByCreatedDateDesc(Wallet wallet);
}
