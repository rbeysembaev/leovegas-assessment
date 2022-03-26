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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Security is omitted for the sake of simplicity. Every method is meant to check if the user has the privileges to perform the operation
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
    public TransactionDTO debit(UUID walletExternalId, UUID transactionExternalId, long amount) {
        return changeBalance(walletExternalId, transactionExternalId, BigDecimal.valueOf(amount).negate());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionDTO credit(UUID walletExternalId, UUID transactionExternalId, long amount) {
        return changeBalance(walletExternalId, transactionExternalId, BigDecimal.valueOf(amount));
    }

    // Pagination is omitted for simplicity
    @Override
    public List<TransactionDTO> listTransactions(UUID walletExternalId) {
        Wallet wallet = walletRepository.findByExternalId(walletExternalId).orElseThrow(CustomNotFoundException::new);
        return transactionMapper.toDTOs(transactionRepository.findAllByWalletOrderByCreatedDateDesc(wallet));
    }

    private TransactionDTO changeBalance(UUID walletExternalId, UUID transactionExternalId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByExternalId(walletExternalId).orElseThrow(CustomNotFoundException::new);

        // If the transaction is a duplicate of an existing one, it can be a retry (e.g. due to a network error)
        // and it makes sense to process it idempotently (therefore HTTP PUT is used).
        // Otherwise, the request is invalid as different transactions can't have the same id.
        // In other words, extenalId is used as an idempotency key here
        Optional<Transaction> existingTransactionOptional = transactionRepository.findByExternalId(transactionExternalId);
        if (existingTransactionOptional.isPresent()) {
            Transaction existingTransaction = existingTransactionOptional.get();
            if (existingTransaction.getWallet().getId().equals(wallet.getId())
                    && existingTransaction.getBalanceChange().equals(amount)) {
                return transactionMapper.toDTO(existingTransaction);
            } else {
                throw new CustomBadRequestException("Invalid transaction");
            }
        }

        // In a real project, I would use database migrations and there would be a constraint preventing negative balances
        // as a safety measure
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Negative balance for wallet " + wallet.getId());
        }

        BigDecimal updatedBalance = wallet.getBalance().add(amount);
        if (updatedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomBadRequestException("Insufficient funds");
        }
        wallet.setBalance(updatedBalance);

        Transaction transaction = new Transaction();
        transaction.setExternalId(transactionExternalId);
        transaction.setWallet(wallet);
        transaction.setBalanceChange(amount);

        // If the externalId uniqueness constraint fails at the transaction commit, it's an unexpected situation,
        // and it's correct to respond with 500. Since the wallet is locked due to transaction isolation,
        // the only way it can happen is if multiple transactions with the same externalId
        // are applied to different wallets, which means that the system is clearly not working as intended
        transactionRepository.save(transaction);

        return transactionMapper.toDTO(transaction);
    }
}
