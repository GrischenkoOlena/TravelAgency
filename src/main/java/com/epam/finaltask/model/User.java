package com.epam.finaltask.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<Order> orders;

    private String phoneNumber;

    private Double balance;

    @Column(name = "account_status")
    private boolean active;

}