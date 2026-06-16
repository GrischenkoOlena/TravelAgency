package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	public static final String ERROR_MESSAGE = "user with name: '%s' doesn't found";
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDTO register(UserDTO userDTO) {
		String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
		User newUser = userMapper.toUser(userDTO);
		newUser.setPassword(encodedPassword);
		return userMapper.toUserDTO(userRepository.save(newUser));
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
		String username = userDTO.getUsername();
		User updateUser = userRepository.findUserByUsername(username)
				.orElseThrow(()-> new EntityNotFoundException(String.format(ERROR_MESSAGE, username)));
		updateUser.setActive(!updateUser.isActive());
		return userMapper.toUserDTO(userRepository.save(updateUser));
	}

	@Override
	public UserDTO getUserById(UUID id) {
		return userMapper.toUserDTO(userRepository.findById(id)
				.orElseThrow(()-> new EntityNotFoundException("user doesn't found in table")));
	}

	@Override
	public List<UserDTO> findAll(){
		return userRepository.findAll().stream().map(userMapper::toUserDTO).toList();
	}

}
