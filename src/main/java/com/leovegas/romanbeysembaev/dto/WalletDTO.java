package com.leovegas.romanbeysembaev.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class WalletDTO {
    UUID externalId;
    Long balance;
    LocalDateTime createdDate;
}
