package com.booking.hotel.bookingservice.repository;

import com.booking.hotel.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
