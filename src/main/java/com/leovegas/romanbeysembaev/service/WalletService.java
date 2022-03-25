package com.leovegas.romanbeysembaev.service;

import com.leovegas.romanbeysembaev.dto.TransactionDTO;
import com.leovegas.romanbeysembaev.dto.WalletDTO;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    WalletDTO createWallet();
    WalletDTO getWallet(UUID walletExternalId);
    TransactionDTO debit(UUID walletExternalId, UUID transactionExternalId, long amount);
    TransactionDTO credit(UUID walletExternalId, UUID transactionExternalId, long amount);
    List<TransactionDTO> listTransactions(UUID walletExternalId);
}
