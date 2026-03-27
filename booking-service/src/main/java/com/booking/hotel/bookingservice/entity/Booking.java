package com.booking.hotel.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    private BookingState state = BookingState.WAITING_FOR_PAYMENT;

    private LocalDate enteringDate;
    private LocalDate leavingDate;

    private Double cost;
}
