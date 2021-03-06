package com.leovegas.romanbeysembaev.mapper;

import com.leovegas.romanbeysembaev.dto.WalletDTO;
import com.leovegas.romanbeysembaev.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper
public interface WalletMapper {
    WalletDTO toDTO(Wallet source);
}
