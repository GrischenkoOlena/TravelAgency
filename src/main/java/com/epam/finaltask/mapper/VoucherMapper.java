package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Voucher;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    @Mapping(source = "isHot", target = "hot")
    Voucher toVoucher(VoucherDTO voucherDTO);

    @Mapping(source = "hot", target = "isHot")
    VoucherDTO toVoucherDTO(Voucher voucher);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "isHot", target = "hot")
    void updateEntityFromDto(VoucherDTO dto, @MappingTarget Voucher entity);
}
