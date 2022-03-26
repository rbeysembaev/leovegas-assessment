package com.leovegas.romanbeysembaev.service.impl;

import com.leovegas.romanbeysembaev.dto.TransactionDTO;
import com.leovegas.romanbeysembaev.dto.WalletDTO;
import com.leovegas.romanbeysembaev.entity.Transaction;
import com.leovegas.romanbeysembaev.entity.Wallet;
import com.leovegas.romanbeysembaev.exception.CustomBadRequestException;
import com.leovegas.romanbeysembaev.exception.CustomNotFoundException;
import com.leovegas.romanbeysembaev.mapper.TransactionMapper;
import com.leovegas.romanbeysembaev.mapper.WalletMapper;
import com.leovegas.romanbeysembaev.repository.TransactionRepository;
import com.leovegas.romanbeysembaev.repository.WalletRepository;
import com.leovegas.romanbeysembaev.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Security is omitted for the sake of simplicity. Every method is meant to check if the user has the privileges required
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    private final TransactionMapper transactionMapper;
    private final WalletMapper walletMapper;

    @Override
    public WalletDTO createWallet() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        // In a real project, the UUID would be generated in a more sophisticated way,
        // based on the microservice instance id or something
        wallet.setExternalId(UUID.randomUUID());
        walletRepository.save(wallet);
        return walletMapper.toDTO(wallet);
    }

    @Override
    public WalletDTO getWallet(UUID walletExternalId) {
        return walletRepository.findByExternalId(walletExternalId)
                .map(walletMapper::toDTO)
                .orElseThrow(CustomNotFoundException::new);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionDTO debit(UUID walletExternalId, UUID transactionExternalId, long amountLong) {
        BigDecimal amount = BigDecimal.valueOf(amountLong);

        Wallet wallet = walletRepository.findByExternalId(walletExternalId).orElseThrow(CustomNotFoundException::new);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new CustomBadRequestException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        transactionRepository.findByExternalId(transactionExternalId)
                .ifPresent(transaction -> {
                    throw new CustomBadRequestException("Invalid transaction ID");
                });

        Transaction transaction = new Transaction();
        transaction.setExternalId(transactionExternalId);
        transaction.setWallet(wallet);
        transaction.setBalanceChange(amount.negate());

        try {
            transactionRepository.save(transaction);
        } catch (ConstraintViolationException e) {
            // There should be a check if it's the external id constraint
            throw new CustomBadRequestException("Invalid transaction ID");
        }

        return transactionMapper.toDTO(transaction);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionDTO credit(UUID walletExternalId, UUID transactionExternalId, long amountLong) {
        BigDecimal amount = BigDecimal.valueOf(amountLong);
        Wallet wallet = walletRepository.findByExternalId(walletExternalId).orElseThrow(CustomNotFoundException::new);

        wallet.setBalance(wallet.getBalance().add(amount));

        transactionRepository.findByExternalId(transactionExternalId)
                .ifPresent(transaction -> {
                    throw new CustomBadRequestException("Invalid transaction ID");
                });

        Transaction transaction = new Transaction();
        transaction.setExternalId(transactionExternalId);
        transaction.setWallet(wallet);
        transaction.setBalanceChange(amount);

        try {
            transactionRepository.save(transaction);
        } catch (ConstraintViolationException e) {
            // There should be a check if it's the external id constraint
            throw new CustomBadRequestException("Invalid transaction ID");
        }

        return transactionMapper.toDTO(transaction);

    }

    // Pagination is omitted for simplicity
    @Override
    public List<TransactionDTO> listTransactions(UUID walletExternalId) {
        Wallet wallet = walletRepository.findByExternalId(walletExternalId).orElseThrow(CustomNotFoundException::new);
        return transactionMapper.toDTOs(transactionRepository.findAllByWalletOrderByCreatedDateDesc(wallet));
    }
}
