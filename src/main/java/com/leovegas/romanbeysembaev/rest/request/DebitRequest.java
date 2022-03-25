package com.leovegas.romanbeysembaev.rest.request;

import lombok.Value;

import java.util.UUID;

@Value
public class DebitRequest {
    UUID transactionExternalId;
    long amount;
}
