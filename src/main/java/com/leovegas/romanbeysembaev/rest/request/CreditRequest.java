package com.leovegas.romanbeysembaev.rest.request;

import lombok.Value;

import java.util.UUID;

@Value
public class CreditRequest {
    UUID transactionExternalId;
    long amount;
}
