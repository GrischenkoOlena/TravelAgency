package com.epam.finaltask.dto;

import com.epam.finaltask.model.User;
import lombok.Data;

@Data
public class UserProfileDTO {
    private String username;

    private String password;

    private String phoneNumber;

    private Double balance;

    public User toUser(User updateUser){
        updateUser.setUsername(username);
        updateUser.setPassword(password);
        updateUser.setPhoneNumber(phoneNumber);
        updateUser.setBalance(balance);

        return updateUser;
    }

}
