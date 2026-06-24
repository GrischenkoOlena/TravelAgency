package com.epam.finaltask.model;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String description;

    private Double price;

    private TourType tourType;

    private TransferType transferType;

    private HotelType hotelType;

    private LocalDate arrivalDate;

    private LocalDate evictionDate;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<Order> orders;

    private boolean isHot;


}
