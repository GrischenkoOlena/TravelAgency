package com.epam.finaltask.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

	private String id;

	@NotBlank(message = "{user.username.blank}")
	@Size(min = 3, max = 20, message = "{user.username.size}")
	private String username;

	@NotBlank(message = "{user.password.blank}")
	@Size(min = 3, max = 20, message = "{user.password.size}")
	private String password;

	private String role;

	private List<OrderDTO> orders;

	private String phoneNumber;

	@Min(value = 0, message = "{user.balance.negative}")
	private Double balance;

	private boolean active;

}
