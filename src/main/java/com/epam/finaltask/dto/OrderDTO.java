package com.epam.finaltask.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private String id;

    private String username;

    private String voucherTitle;

    private Double price;

    private String status;
}
