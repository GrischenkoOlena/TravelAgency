package com.epam.finaltask.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderDTO {
    private String id;

    private String username;

    private String voucherTitle;

    @Positive(message = "Price must be positive")
    private Double price;

    private String status;
}
