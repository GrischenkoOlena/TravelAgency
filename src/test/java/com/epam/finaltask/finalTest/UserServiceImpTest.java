package com.epam.finaltask.finalTest;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.NotEnoughMoneyException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.OrderRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID sampleUuid;
    private String sampleUuidStr;
    private User sampleUser;
    private UserDTO sampleUserDto;

    @BeforeEach
    void setUp() {
        sampleUuid = UUID.randomUUID();
        sampleUuidStr = sampleUuid.toString();

        sampleUser = new User();
        sampleUser.setId(sampleUuid);
        sampleUser.setUsername("testUser");
        sampleUser.setPassword("plainPassword");
        sampleUser.setBalance(100.00);
        sampleUser.setActive(true);

        sampleUserDto = new UserDTO();
        sampleUserDto.setId(sampleUuidStr);
        sampleUserDto.setUsername("testUser");
        sampleUserDto.setPassword("plainPassword");
        sampleUserDto.setBalance(100.00);
    }

    @Nested
    class RegisterAndCreateManagerTests {
        @Test
        void register_ShouldSaveUserWithUserRole() {
            when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
            when(userMapper.toUser(sampleUserDto)).thenReturn(sampleUser);
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            UserDTO result = userService.register(sampleUserDto);

            assertNotNull(result);
            verify(passwordEncoder).encode("plainPassword");
            verify(userRepository).save(argThat(user ->
                    user.getRole() == Role.USER &&
                            user.getBalance() == 0.00 &&
                            user.isActive()
            ));
        }

        @Test
        void createManager_ShouldSaveUserWithManagerRoleAndPreserveBalance() {
            when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
            when(userMapper.toUser(sampleUserDto)).thenReturn(sampleUser);
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            UserDTO result = userService.createManager(sampleUserDto);

            assertNotNull(result);
            verify(userRepository).save(argThat(user ->
                    user.getRole() == Role.MANAGER &&
                            user.getBalance() == 100.00
            ));
        }
    }

    @Nested
    class DeleteUserTests {
        @Test
        void deleteUser_ShouldDeleteWhenUserExists() {
            when(userRepository.findById(sampleUuid)).thenReturn(Optional.of(sampleUser));

            userService.deleteUser(sampleUuidStr);

            verify(userRepository).delete(sampleUser);
        }

        @Test
        void deleteUser_ShouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(sampleUuid)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(sampleUuidStr));
            verify(userRepository, never()).delete(any(User.class));
        }
    }

    @Nested
    class UpdateUserTests {
        @Test
        void updateUser_ShouldUpdateWhenUserExists() {
            String username = "testUser";
            when(userRepository.existsByUsername(username)).thenReturn(true);
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(sampleUser));
            when(userRepository.save(sampleUser)).thenReturn(sampleUser);
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            UserDTO result = userService.updateUser(username, sampleUserDto);

            assertNotNull(result);
            verify(userMapper).updateEntityFromDTO(sampleUserDto, sampleUser);
            verify(userRepository).save(sampleUser);
        }

        @Test
        void updateUser_ShouldThrowExceptionWhenUserDoesNotExist() {
            String username = "ghostUser";
            when(userRepository.existsByUsername(username)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> userService.updateUser(username, sampleUserDto));
        }
    }

    @Nested
    class GetUserTests {
        @Test
        void getUserByUsername_ShouldReturnUserDto() {
            String username = "testUser";
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(sampleUser));
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            UserDTO result = userService.getUserByUsername(username);

            assertEquals(sampleUserDto, result);
        }

        @Test
        void getUserById_ShouldReturnUserDto() {
            when(userRepository.findById(sampleUuid)).thenReturn(Optional.of(sampleUser));
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            UserDTO result = userService.getUserById(sampleUuid);

            assertEquals(sampleUserDto, result);
        }

        @Test
        void findAll_ShouldReturnList() {
            when(userRepository.findAll()).thenReturn(List.of(sampleUser));
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            List<UserDTO> result = userService.findAll();

            assertEquals(1, result.size());
        }
    }

    @Nested
    class ChangeAccountStatusTests {
        @Test
        void changeAccountStatus_ShouldToggleActiveFlag() {
            sampleUser.setActive(true);
            when(userRepository.findById(sampleUuid)).thenReturn(Optional.of(sampleUser));
            when(userRepository.save(sampleUser)).thenReturn(sampleUser);
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            userService.changeAccountStatus(sampleUuidStr);

            assertFalse(sampleUser.isActive());
            verify(userRepository).save(sampleUser);
        }
    }

    @Nested
    class LoadUserByUsernameTests {
        @Test
        void loadUserByUsername_ShouldReturnUserDetails() {
            String username = "testUser";
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(sampleUser));

            UserDetails result = userService.loadUserByUsername(username);

            assertNotNull(result);
            assertEquals(username, result.getUsername());
        }

        @Test
        void loadUserByUsername_ShouldThrowUsernameNotFoundException() {
            String username = "ghost";
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        }
    }

    @Nested
    class PayOrderTests {
        private Order sampleOrder;
        private UUID orderUuid;
        private String orderUuidStr;

        @BeforeEach
        void setUpOrder() {
            orderUuid = UUID.randomUUID();
            orderUuidStr = orderUuid.toString();

            Voucher sampleVoucher = new Voucher();
            sampleVoucher.setPrice(40.00);

            sampleOrder = new Order();
            sampleOrder.setId(orderUuid);
            sampleOrder.setVoucher(sampleVoucher);
            sampleOrder.setStatus(VoucherStatus.REGISTERED);
        }

        @Test
        void payOrder_ShouldDeductBalanceAndSetStatusToPaid() {
            when(orderRepository.findById(orderUuid)).thenReturn(Optional.of(sampleOrder));
            when(userRepository.findById(sampleUuid)).thenReturn(Optional.of(sampleUser));
            when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);
            when(userRepository.save(sampleUser)).thenReturn(sampleUser);
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            UserDTO result = userService.payOrder(orderUuidStr, sampleUserDto);

            assertNotNull(result);
            assertEquals(60.00, sampleUser.getBalance()); // 100.00 - 40.00
            assertEquals(VoucherStatus.PAID, sampleOrder.getStatus());
            verify(orderRepository).save(sampleOrder);
            verify(userRepository).save(sampleUser);
        }

        @Test
        void payOrder_ShouldThrowExceptionWhenBalanceIsInsufficient() {
            sampleUser.setBalance(20.00);
            when(orderRepository.findById(orderUuid)).thenReturn(Optional.of(sampleOrder));
            when(userRepository.findById(sampleUuid)).thenReturn(Optional.of(sampleUser));

            assertThrows(NotEnoughMoneyException.class, () -> userService.payOrder(orderUuidStr, sampleUserDto));

            verify(orderRepository, never()).save(any(Order.class));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class UpdateProfileTests {
        @Mock
        private UserProfileDTO userProfileDTO;

        @Test
        void updateProfile_ShouldEncodePasswordAndSave() {
            String username = "testUser";
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(sampleUser));
            when(passwordEncoder.encode(any())).thenReturn("newEncodedPassword");
            when(userRepository.save(sampleUser)).thenReturn(sampleUser);
            when(userMapper.toUserDTO(sampleUser)).thenReturn(sampleUserDto);

            UserDTO result = userService.updateProfile(username, userProfileDTO);

            assertNotNull(result);
            verify(userProfileDTO).toUser(sampleUser);
            verify(passwordEncoder).encode(any());
            verify(userRepository).save(sampleUser);
        }
    }
}
