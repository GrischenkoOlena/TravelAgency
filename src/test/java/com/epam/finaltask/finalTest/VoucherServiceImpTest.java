package com.epam.finaltask.finalTest;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.OrderRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.service.implementation.VoucherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTest {

    @Mock
    private VoucherRepository voucherRepo;
    @Mock
    private VoucherMapper voucherMapper;
    @Mock
    private UserRepository userRepo;
    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    private UUID sampleVoucherId;
    private String sampleVoucherIdStr;
    private UUID sampleUserId;
    private String sampleUserIdStr;

    private Voucher sampleVoucher;
    private VoucherDTO sampleVoucherDto;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleVoucherId = UUID.randomUUID();
        sampleVoucherIdStr = sampleVoucherId.toString();
        sampleUserId = UUID.randomUUID();
        sampleUserIdStr = sampleUserId.toString();

        sampleVoucher = new Voucher();
        sampleVoucher.setId(sampleVoucherId);
        sampleVoucher.setHot(false);

        sampleVoucherDto = new VoucherDTO();
        sampleVoucherDto.setIsHot(true);

        sampleUser = new User();
        sampleUser.setId(sampleUserId);
    }

    @Nested
    class CreateTests {
        @Test
        void create_ShouldSaveAndReturnVoucherDTO() {
            when(voucherMapper.toVoucher(sampleVoucherDto)).thenReturn(sampleVoucher);
            when(voucherRepo.save(sampleVoucher)).thenReturn(sampleVoucher);
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            VoucherDTO result = voucherService.create(sampleVoucherDto);

            assertNotNull(result);
            verify(voucherRepo).save(sampleVoucher);
        }
    }

    @Nested
    class OrderTests {
        @Test
        void order_ShouldCreateOrderAndReturnVoucher() {
            when(voucherRepo.findById(sampleVoucherId)).thenReturn(Optional.of(sampleVoucher));
            when(userRepo.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));
            when(orderRepo.save(any(Order.class))).thenReturn(new Order());
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            VoucherDTO result = voucherService.order(sampleVoucherIdStr, sampleUserIdStr);

            assertNotNull(result);
            verify(orderRepo).save(argThat(order ->
                    order.getUser().equals(sampleUser) &&
                            order.getVoucher().equals(sampleVoucher) &&
                            order.getStatus() == VoucherStatus.REGISTERED
            ));
            // Verifies both findById calls in the method
            verify(voucherRepo, times(2)).findById(sampleVoucherId);
        }

        @Test
        void order_ShouldThrowExceptionWhenVoucherNotFound() {
            when(voucherRepo.findById(sampleVoucherId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> voucherService.order(sampleVoucherIdStr, sampleUserIdStr));
            verifyNoInteractions(userRepo, orderRepo);
        }

        @Test
        void order_ShouldThrowExceptionWhenUserNotFound() {
            when(voucherRepo.findById(sampleVoucherId)).thenReturn(Optional.of(sampleVoucher));
            when(userRepo.findById(sampleUserId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> voucherService.order(sampleVoucherIdStr, sampleUserIdStr));
            verifyNoInteractions(orderRepo);
        }
    }

    @Nested
    class UpdateAndChangeStatusTests {
        @Test
        void update_ShouldModifyAndSaveVoucher() {
            when(voucherRepo.findById(sampleVoucherId)).thenReturn(Optional.of(sampleVoucher));
            when(voucherRepo.save(sampleVoucher)).thenReturn(sampleVoucher);
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            VoucherDTO result = voucherService.update(sampleVoucherIdStr, sampleVoucherDto);

            assertNotNull(result);
            verify(voucherMapper).updateEntityFromDto(sampleVoucherDto, sampleVoucher);
            verify(voucherRepo).save(sampleVoucher);
        }

        @Test
        void changeHotStatus_ShouldToggleHotProperty() {
            when(voucherRepo.findById(sampleVoucherId)).thenReturn(Optional.of(sampleVoucher));
            when(voucherRepo.save(sampleVoucher)).thenReturn(sampleVoucher);
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            voucherService.changeHotStatus(sampleVoucherIdStr, sampleVoucherDto);

            assertTrue(sampleVoucher.isHot());
            verify(voucherRepo).save(sampleVoucher);
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void delete_ShouldRemoveVoucherWhenExists() {
            when(voucherRepo.findById(sampleVoucherId)).thenReturn(Optional.of(sampleVoucher));

            voucherService.delete(sampleVoucherIdStr);

            verify(voucherRepo).delete(sampleVoucher);
        }

        @Test
        void delete_ShouldThrowExceptionWhenNotFound() {
            when(voucherRepo.findById(sampleVoucherId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> voucherService.delete(sampleVoucherIdStr));
            verify(voucherRepo, never()).delete(any(Voucher.class));
        }
    }

    @Nested
    class FinderTests {
        @Test
        void findAllByUserId_ShouldReturnList() {
            when(voucherRepo.findAllByUserId(sampleUserId)).thenReturn(List.of(sampleVoucher));
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            List<VoucherDTO> result = voucherService.findAllByUserId(sampleUserIdStr);

            assertEquals(1, result.size());
        }

        @Test
        void findAllByTourType_ShouldReturnList() {
            when(voucherRepo.findAllByTourType(TourType.ADVENTURE)).thenReturn(List.of(sampleVoucher));
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            List<VoucherDTO> result = voucherService.findAllByTourType(TourType.ADVENTURE);

            assertEquals(1, result.size());
        }

        @Test
        void findAllByTransferType_ShouldParseStringAndReturnList() {
            when(voucherRepo.findAllByTransferType(TransferType.BUS)).thenReturn(List.of(sampleVoucher));
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            List<VoucherDTO> result = voucherService.findAllByTransferType("BUS");

            assertEquals(1, result.size());
        }

        @Test
        void findAllByPrice_ShouldReturnList() {
            when(voucherRepo.findAllByPrice(500.0)).thenReturn(List.of(sampleVoucher));
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            List<VoucherDTO> result = voucherService.findAllByPrice(500.0);

            assertEquals(1, result.size());
        }

        @Test
        void findAllByHotelType_ShouldReturnList() {
            when(voucherRepo.findAllByHotelType(HotelType.FIVE_STARS)).thenReturn(List.of(sampleVoucher));
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            List<VoucherDTO> result = voucherService.findAllByHotelType(HotelType.FIVE_STARS);

            assertEquals(1, result.size());
        }

        @Test
        void findAll_ShouldReturnAllItems() {
            when(voucherRepo.findAll()).thenReturn(List.of(sampleVoucher));
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            List<VoucherDTO> result = voucherService.findAll();

            assertEquals(1, result.size());
        }

        @Test
        void findById_ShouldReturnDto() {
            when(voucherRepo.findById(sampleVoucherId)).thenReturn(Optional.of(sampleVoucher));
            when(voucherMapper.toVoucherDTO(sampleVoucher)).thenReturn(sampleVoucherDto);

            VoucherDTO result = voucherService.findById(sampleVoucherIdStr);

            assertNotNull(result);
        }
    }
}
