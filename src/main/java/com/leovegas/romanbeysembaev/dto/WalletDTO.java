package com.leovegas.romanbeysembaev.dto;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class WalletDTO {
    UUID externalId;
    Long balance;
    LocalDateTime createdDate;
}
