package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.dto.UserUpdateDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDTO register(UserDTO userDTO);
    void deleteUser(String userId);
    UserDTO updateUser(String username, UserUpdateDTO userDTO);

    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(String userId);
    UserDTO getUserById(UUID id);

    List<UserDTO> findAll();

    UserDTO payOrder(String orderId, UserDTO userDTO);

    UserDTO updateProfile(String username, UserProfileDTO userProfileDTO);

    UserDTO createManager(UserDTO userDTO);
}
