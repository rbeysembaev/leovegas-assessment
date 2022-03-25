package com.leovegas.romanbeysembaev.dto;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class TransactionDTO {
    UUID externalId;
    Long balanceChange;
    LocalDateTime createdDate;
}

