package com.epam.finaltask.finalTest;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.UnableChangeStatusException;
import com.epam.finaltask.mapper.OrderMapper;
import com.epam.finaltask.model.Order;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.OrderRepository;
import com.epam.finaltask.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID sampleUuid;
    private String sampleUuidStr;
    private Order sampleOrder;
    private OrderDTO sampleOrderDto;

    @BeforeEach
    void setUp() {
        sampleUuid = UUID.randomUUID();
        sampleUuidStr = sampleUuid.toString();

        sampleOrder = new Order();
        sampleOrder.setId(sampleUuid);
        sampleOrder.setStatus(VoucherStatus.REGISTERED);

        sampleOrderDto = new OrderDTO();
    }

    @Nested
    class FindOrdersByUserIdTests {
        @Test
        void shouldReturnListOfOrderDTOsWhenUserIdExists() {
            // Given
            when(orderRepository.findAllByUserId(sampleUuid)).thenReturn(List.of(sampleOrder));
            when(orderMapper.toOrderDTO(sampleOrder)).thenReturn(sampleOrderDto);

            // When
            List<OrderDTO> result = orderService.findOrdersByUserId(sampleUuidStr);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(sampleOrderDto, result.get(0));
            verify(orderRepository, times(1)).findAllByUserId(sampleUuid);
            verify(orderMapper, times(1)).toOrderDTO(sampleOrder);
        }

        @Test
        void shouldReturnEmptyListWhenNoOrdersFoundForUser() {
            when(orderRepository.findAllByUserId(sampleUuid)).thenReturn(Collections.emptyList());

            List<OrderDTO> result = orderService.findOrdersByUserId(sampleUuidStr);

            assertTrue(result.isEmpty());
            verify(orderRepository, times(1)).findAllByUserId(sampleUuid);
            verifyNoInteractions(orderMapper);
        }
    }

    @Nested
    class FindAllTests {
        @Test
        void shouldReturnAllOrders() {
            when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
            when(orderMapper.toOrderDTO(sampleOrder)).thenReturn(sampleOrderDto);

            List<OrderDTO> result = orderService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(orderRepository, times(1)).findAll();
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void shouldReturnOrderDTOWhenOrderExists() {
            when(orderRepository.findById(sampleUuid)).thenReturn(Optional.of(sampleOrder));
            when(orderMapper.toOrderDTO(sampleOrder)).thenReturn(sampleOrderDto);

            OrderDTO result = orderService.findById(sampleUuidStr);

            assertNotNull(result);
            assertEquals(sampleOrderDto, result);
            verify(orderRepository, times(1)).findById(sampleUuid);
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenOrderDoesNotExist() {
            when(orderRepository.findById(sampleUuid)).thenReturn(Optional.empty());

            EntityNotFoundException exception =
                    assertThrows(EntityNotFoundException.class, () -> orderService.findById(sampleUuidStr));

            assertEquals("not found order with id: " + sampleUuidStr, exception.getMessage());
            verify(orderRepository, times(1)).findById(sampleUuid);
            verifyNoInteractions(orderMapper);
        }
    }

    @Nested
    class CanceledOrderTests {
        @Test
        void shouldCancelOrderSuccessfullyWhenStatusIsNotPaid() {
            // Given
            sampleOrder.setStatus(VoucherStatus.REGISTERED);
            when(orderRepository.findById(sampleUuid)).thenReturn(Optional.of(sampleOrder));
            when(orderMapper.toOrderDTO(sampleOrder)).thenReturn(sampleOrderDto);

            // When
            OrderDTO result = orderService.canceledOrder(sampleUuidStr);

            // Then
            assertNotNull(result);
            assertEquals(VoucherStatus.CANCELED, sampleOrder.getStatus());
            verify(orderRepository, times(1)).save(sampleOrder);
            verify(orderRepository, times(2)).findById(sampleUuid);
        }

        @Test
        void shouldThrowUnableChangeStatusExceptionWhenOrderIsPaid() {
            // Given
            sampleOrder.setStatus(VoucherStatus.PAID);
            when(orderRepository.findById(sampleUuid)).thenReturn(Optional.of(sampleOrder));

            // When & Then
            UnableChangeStatusException exception =
                    assertThrows(UnableChangeStatusException.class, () -> orderService.canceledOrder(sampleUuidStr));

            assertEquals("unable to cancel paid order", exception.getMessage());
            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenCancelingNonExistentOrder() {
            when(orderRepository.findById(sampleUuid)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> orderService.canceledOrder(sampleUuidStr));

            verify(orderRepository, never()).save(any(Order.class));
        }
    }
}
