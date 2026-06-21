package com.epam.finaltask.service;

import com.epam.finaltask.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> findOrdersByUserId(String userId);
    List<OrderDTO> findAll();

    OrderDTO findById(String orderId);

    OrderDTO canceledOrder(String orderId);
}
