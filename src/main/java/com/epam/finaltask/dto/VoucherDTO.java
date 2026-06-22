package com.epam.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class VoucherDTO {

    private String id;

    @NotBlank(message = "{voucher.title.blank}")
    private String title;

    @NotBlank(message = "{voucher.description.blank}")
    private String description;

    @Positive(message = "{voucher.price.positive}")
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
