package com.epam.finaltask.service.implementation;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.UnableChangeStatusException;
import com.epam.finaltask.mapper.OrderMapper;
import com.epam.finaltask.model.Order;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.OrderRepository;
import com.epam.finaltask.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private OrderMapper orderMapper;
    @Override
    public List<OrderDTO> findOrdersByUserId(String userId) {
        return orderRepository
                .findAllByUserId(UUID.fromString(userId))
                .stream()
                .map(orderMapper::toOrderDTO)
                .toList();
    }

    @Override
    public List<OrderDTO> findAll() {
        return orderRepository
                .findAll()
                .stream()
                .map(orderMapper::toOrderDTO)
                .toList();
    }

    @Override
    public OrderDTO findById(String orderId) {
        return orderMapper.toOrderDTO(orderRepository
                .findById(UUID.fromString(orderId))
                .orElseThrow(()-> new EntityNotFoundException("not found order with id: " + orderId)));
    }

    @Override
    public OrderDTO canceledOrder(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(()-> new EntityNotFoundException("not found order with id: " + orderId));
        VoucherStatus orderStatus = order.getStatus();
        if (orderStatus == VoucherStatus.PAID){
            throw new UnableChangeStatusException("unable to cancel paid order");
        } else {
            order.setStatus(VoucherStatus.CANCELED);
            orderRepository.save(order);
        }
        return findById(orderId);
    }
}
