package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "username", target = "user.username")
    @Mapping(source = "voucherTitle", target = "voucher.title")
    @Mapping(source = "price", target = "voucher.price")
    Order toOrder(OrderDTO orderDTO);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "voucher.title", target = "voucherTitle")
    @Mapping(source = "voucher.price", target = "price")
    OrderDTO toOrderDTO(Order order);
}
