package com.leovegas.romanbeysembaev.mapper;

import com.leovegas.romanbeysembaev.dto.TransactionDTO;
import com.leovegas.romanbeysembaev.entity.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {
    Transaction toEntity(TransactionDTO source);
    TransactionDTO toDTO(Transaction source);
    List<TransactionDTO> toDTOs(Iterable<Transaction> source);
}
