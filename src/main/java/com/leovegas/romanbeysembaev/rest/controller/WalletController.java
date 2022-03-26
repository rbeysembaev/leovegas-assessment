package com.leovegas.romanbeysembaev.rest.controller;

import com.leovegas.romanbeysembaev.dto.TransactionDTO;
import com.leovegas.romanbeysembaev.dto.WalletDTO;
import com.leovegas.romanbeysembaev.rest.request.CreditRequest;
import com.leovegas.romanbeysembaev.rest.request.DebitRequest;
import com.leovegas.romanbeysembaev.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Validation is omitted for the sake of simplicity
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // In a real project, this method would accept a Request object with the user's external id, wallet name etc
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletDTO createWallet() {
        return walletService.createWallet();
    }

    // External wallet id is used instead of user id because the user might have multiple wallets (i.e. in different currencies)
    @GetMapping("/{externalId}")
    public WalletDTO getWallet(@PathVariable UUID externalId) {
        return walletService.getWallet(externalId);
    }

    // Debit and credit are two separate endpoints/service methods because the difference in their implementations might
    // become bigger (e.g. extra checks or even locks for debit)
    @PutMapping("/{walletExternalId}/debit")
    public TransactionDTO debit(@PathVariable UUID walletExternalId, @RequestBody DebitRequest request) {
        return walletService.debit(walletExternalId, request.getTransactionExternalId(), request.getAmount());
    }

    @PutMapping("/{walletExternalId}/credit")
    public TransactionDTO credit(@PathVariable UUID walletExternalId, @RequestBody CreditRequest request) {
        return walletService.credit(walletExternalId, request.getTransactionExternalId(), request.getAmount());
    }

    @GetMapping("/{walletExternalId}/transaction")
    public List<TransactionDTO> listTransactions(@PathVariable UUID walletExternalId) {
        return walletService.listTransactions(walletExternalId);
    }
}
