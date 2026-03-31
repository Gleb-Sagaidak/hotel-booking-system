package com.booking.hotel.clientservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "clients")
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String phoneNumber;
}
