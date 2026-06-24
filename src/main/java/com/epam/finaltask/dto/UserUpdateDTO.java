package com.epam.finaltask.dto;

import com.epam.finaltask.model.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "{user.username.blank}")
    @Size(min = 3, max = 20, message = "{user.username.size}")
    private String username;

    private String phoneNumber;

    @Min(value = 0, message = "{user.balance.negative}")
    private Double balance;

    public User toUser(User updateUser){
        updateUser.setUsername(username);
        updateUser.setPhoneNumber(phoneNumber);
        updateUser.setBalance(balance);

        return updateUser;
    }
}
