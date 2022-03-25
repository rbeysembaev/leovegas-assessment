package com.leovegas.romanbeysembaev.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
    @CreationTimestamp
    private LocalDateTime createdDate;
}
