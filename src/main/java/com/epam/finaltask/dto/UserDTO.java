package com.epam.finaltask.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDTO {

	private String id;

	private String username;

	private String password;

	private String role;

	private List<OrderDTO> orders;

	private String phoneNumber;

	private Double balance;

	private boolean active;

}
