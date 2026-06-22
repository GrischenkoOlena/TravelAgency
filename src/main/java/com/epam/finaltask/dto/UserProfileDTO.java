package com.epam.finaltask.dto;

import com.epam.finaltask.model.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileDTO {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
    private String password;

    private String phoneNumber;

    @Min(value = 0, message = "Balance can't be negative")
    private Double balance;

    public User toUser(User updateUser){
        updateUser.setUsername(username);
        updateUser.setPassword(password);
        updateUser.setPhoneNumber(phoneNumber);
        updateUser.setBalance(balance);

        return updateUser;
    }

}
