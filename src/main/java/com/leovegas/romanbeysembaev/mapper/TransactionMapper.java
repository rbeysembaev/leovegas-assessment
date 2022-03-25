package com.leovegas.romanbeysembaev.mapper;

import com.leovegas.romanbeysembaev.dto.TransactionDTO;
import com.leovegas.romanbeysembaev.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionMapper {
    Transaction toEntity(TransactionDTO source);
    TransactionDTO toDTO(Transaction source);
}
