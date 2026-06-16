package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.RemoteResponse;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.service.UserService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<RemoteResponse<UserDTO>> findAllUsers(){
        RemoteResponse<UserDTO> response = RemoteResponse.success(userService.findAll(), null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<RemoteResponse<UserDTO>> findUserById(@PathVariable String userId){
        UserDTO currentUser = userService.getUserById(UUID.fromString(userId));
        RemoteResponse<UserDTO> response = RemoteResponse.success(List.of(currentUser), null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userName}")
    public ResponseEntity<RemoteResponse<UserDTO>> findUserByName(@PathVariable String userName){
        try {
            UserDTO currentUser = userService.getUserByUsername(userName);
            RemoteResponse<UserDTO> response = RemoteResponse.success(List.of(currentUser), null);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e){
            RemoteResponse<UserDTO> response = RemoteResponse.error(null, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping()
    public ResponseEntity<RemoteResponse<UserDTO>> createUser(@RequestBody UserDTO userDTO){
        UserDTO newUser = userService.register(userDTO);
        RemoteResponse<UserDTO> response = RemoteResponse.success(List.of(newUser),
                "User is successfully registered");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{userName}")
    public ResponseEntity<RemoteResponse<UserDTO>> updateVoucher(@PathVariable String userName, @RequestBody UserDTO userDTO){
        UserDTO updateUser = userService.updateUser(userName, userDTO);
        RemoteResponse<UserDTO> response = RemoteResponse.success(List.of(updateUser),
                "User is successfully updated");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse<UserDTO>> changeAccountStatus(@RequestBody UserDTO userDTO){
        UserDTO updateUser = userService.changeAccountStatus(userDTO);
        RemoteResponse<UserDTO> response = RemoteResponse.success(List.of(updateUser),
                "Status user's account is successfully changed");
        return ResponseEntity.ok(response);
    }
	
}
