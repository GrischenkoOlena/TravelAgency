package com.epam.finaltask.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class VoucherDTO {

    private String id;

    private String title;

    private String description;

    private Double price;

    private String tourType;

    private String transferType;

    private String hotelType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate evictionDate;

    private List<OrderDTO> orders;

    private Boolean isHot;

    
}
