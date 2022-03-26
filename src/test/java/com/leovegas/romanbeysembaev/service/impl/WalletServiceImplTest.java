package com.leovegas.romanbeysembaev.service.impl;

import com.leovegas.romanbeysembaev.dto.TransactionDTO;
import com.leovegas.romanbeysembaev.entity.Transaction;
import com.leovegas.romanbeysembaev.entity.Wallet;
import com.leovegas.romanbeysembaev.exception.CustomNotFoundException;
import com.leovegas.romanbeysembaev.mapper.TransactionMapper;
import com.leovegas.romanbeysembaev.mapper.WalletMapper;
import com.leovegas.romanbeysembaev.repository.TransactionRepository;
import com.leovegas.romanbeysembaev.repository.WalletRepository;
import com.leovegas.romanbeysembaev.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Just a demo test to show that I'm familiar with unit tests. In a real project, I would expect 80%+ coverage
@SpringBootTest(classes = WalletServiceImpl.class)
class WalletServiceImplTest {

    @Autowired private WalletService walletService;

    @MockBean private TransactionRepository transactionRepository;
    @MockBean private WalletRepository walletRepository;
    @MockBean private TransactionMapper transactionMapper;
    @MockBean private WalletMapper walletMapper;

    @Test
    void testPositiveDebit() {
        // GIVEN
        UUID walletExternalId = UUID.randomUUID();
        UUID transactionExternalId = UUID.randomUUID();
        long amount = 100;
        BigDecimal initialBalance = BigDecimal.valueOf(amount).plus();
        Wallet wallet = new Wallet();
        wallet.setBalance(initialBalance);
        TransactionDTO transactionDTO = new TransactionDTO(transactionExternalId, -amount, null);

        ArgumentMatcher<Transaction> transactionMatcher = transaction ->
                transaction.getExternalId() == transactionExternalId
                        && transaction.getWallet() == wallet
                        && transaction.getBalanceChange().equals(BigDecimal.valueOf(amount).negate());

        // WHEN
        when(walletRepository.findByExternalId(walletExternalId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByExternalId(transactionExternalId)).thenReturn(Optional.empty());
        when(transactionMapper.toDTO(argThat(transactionMatcher))).thenReturn(transactionDTO);
        TransactionDTO result = walletService.debit(walletExternalId, transactionExternalId, amount);

        // THEN
        verify(transactionRepository).save(argThat(transactionMatcher));
        assertSame(transactionDTO, result);
        assertEquals(initialBalance.subtract(BigDecimal.valueOf(amount)), wallet.getBalance());
    }

    @Test
    void testNegativeDebitWalletNotFound() {
        // GIVEN
        UUID walletExternalId = UUID.randomUUID();
        UUID transactionExternalId = UUID.randomUUID();
        long amount = 100;

        // THEN
        assertThrows(CustomNotFoundException.class, () -> walletService.debit(walletExternalId, transactionExternalId, amount));
    }
}
