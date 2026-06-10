package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
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
			User updateUser = userRepository.save(userMapper.toUser(userDTO));
			return userMapper.toUserDTO(userRepository.save(updateUser));
		}
		return null;
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		return userMapper.toUserDTO(userRepository.findUserByUsername(username).orElseThrow());
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		User updateUser = userMapper.toUser(userDTO);
		updateUser.setActive(true);
		userRepository.save(updateUser);
		return getUserById(updateUser.getId());
	}

	@Override
	public UserDTO getUserById(UUID id) {
		return userMapper.toUserDTO(userRepository.findById(id).orElseThrow());
	}

	@Override
	public List<UserDTO> findAll(){
		return userRepository.findAll().stream().map(userMapper::toUserDTO).toList();
	}

}
