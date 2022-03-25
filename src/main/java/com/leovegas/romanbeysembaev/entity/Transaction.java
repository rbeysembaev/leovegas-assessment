package com.leovegas.romanbeysembaev.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(indexes = {@Index(columnList = "externalId", unique = true), @Index(columnList = "wallet_id, createdDate")})
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    // external id is meant to be used in the API and is bruteforce-resistant,
    // while id is for internal usage only (i.e. as a foreign key)
    @Column(nullable = false, updatable = false)
    private UUID externalId;

    @Column(nullable = false, updatable = false)
    private BigDecimal balanceChange;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Wallet wallet;
}


