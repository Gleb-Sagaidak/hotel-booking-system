package com.booking.hotel.bookingservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "rooms")
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfo {
    @Id
    private Long roomId;

    private Double pricePerNight;
}
