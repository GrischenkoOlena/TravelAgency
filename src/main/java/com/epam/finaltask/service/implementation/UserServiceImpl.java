package com.epam.finaltask.service.implementation;

import java.util.List;
import java.util.UUID;

import com.epam.finaltask.auth.SecurityUser;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.exception.NotEnoughMoneyException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.Order;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.OrderRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	public static final String ERROR_MESSAGE = "user with id/name: '%s' doesn't found";
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDTO register(UserDTO userDTO) {
		User newUser = createNewUser(userDTO, Role.USER);
		return userMapper.toUserDTO(userRepository.save(newUser));
	}

	public void deleteUser(String userId){
		userRepository.delete(userRepository.findById(UUID.fromString(userId)).orElseThrow());
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		if (userRepository.existsByUsername(username)){
			User updateUser = userRepository.findUserByUsername(username).orElseThrow();
			userMapper.updateEntityFromDTO(userDTO, updateUser);
			return userMapper.toUserDTO(userRepository.save(updateUser));
		} else {
			throw new EntityNotFoundException(String.format(ERROR_MESSAGE, username));
		}
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		return userMapper.toUserDTO(userRepository.findUserByUsername(username)
				.orElseThrow(()-> new EntityNotFoundException(String.format(ERROR_MESSAGE, username))));
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		UUID userId = UUID.fromString(userDTO.getId());
		User updateUser = userRepository.findById(userId)
				.orElseThrow(()-> new EntityNotFoundException(String.format(ERROR_MESSAGE, userId)));
		updateUser.setActive(!updateUser.isActive());
		return userMapper.toUserDTO(userRepository.save(updateUser));
	}

	@Override
	public UserDTO getUserById(UUID id) {
		return userMapper.toUserDTO(userRepository.findById(id)
				.orElseThrow(()-> new EntityNotFoundException(String.format(ERROR_MESSAGE, id))));
	}

	@Override
	public List<UserDTO> findAll(){
		return userRepository.findAll().stream().map(userMapper::toUserDTO).toList();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User currentUser = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Not found user with name " + username));
		return new SecurityUser(currentUser);
	}

	@Override
	@Transactional
	public UserDTO payOrder(String orderId, UserDTO userDTO) {
		Order order = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();

		UUID userId = UUID.fromString(userDTO.getId());
		User updateUser = userRepository.findById(userId)
				.orElseThrow(()-> new EntityNotFoundException(String.format(ERROR_MESSAGE, userId)));

		double newBalance = updateUser.getBalance() - order.getVoucher().getPrice();
		if (newBalance > 0 && order.getStatus() == VoucherStatus.REGISTERED){
			updateUser.setBalance(newBalance);
			order.setStatus(VoucherStatus.PAID);
		} else {
			throw new NotEnoughMoneyException("user doesn't have enough money for buy tour");
		}
		orderRepository.save(order);
		return userMapper.toUserDTO(userRepository.save(updateUser));
	}

	@Override
	public UserDTO updateProfile(String username, UserProfileDTO userProfileDTO) {
		User updateUser = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Not found user with name " + username));
		userProfileDTO.toUser(updateUser);
		updateUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));

		return userMapper.toUserDTO(userRepository.save(updateUser));
	}

	@Override
	public UserDTO createManager(UserDTO userDTO) {
		User newUser = createNewUser(userDTO, Role.MANAGER);
		newUser.setBalance(userDTO.getBalance());
		return userMapper.toUserDTO(userRepository.save(newUser));
	}

	private User createNewUser(UserDTO userDTO, Role role){
		String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
		User resultUser = userMapper.toUser(userDTO);
		resultUser.setPassword(encodedPassword);
		resultUser.setBalance(0.00);
		resultUser.setRole(role);
		resultUser.setActive(true);
		return resultUser;
	}
}
