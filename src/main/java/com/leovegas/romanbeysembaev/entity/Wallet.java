package com.leovegas.romanbeysembaev.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(indexes = @Index(columnList = "externalId"))
@Data
@EqualsAndHashCode(callSuper = true)
public class Wallet extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID externalId;

    @Column(nullable = false)
    private BigDecimal balance;
}
