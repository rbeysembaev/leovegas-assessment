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

    // BigDecimal is used instead of long to make sure fellow developers don't do any monetary operations with long,
    // which could result in invalid calculations due to limited precision
    @Column(nullable = false)
    private BigDecimal balance;
}
