package com.leovegas.romanbeysembaev.repository;

import com.leovegas.romanbeysembaev.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByExternalId(UUID externalId);
}
