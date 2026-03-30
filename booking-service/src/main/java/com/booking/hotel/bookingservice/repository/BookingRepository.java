package com.booking.hotel.bookingservice.repository;

import com.booking.hotel.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select count(b) > 0 from Booking b " +
            "where b.roomId = :roomId " +
            "and b.enteringDate < :leavingDate " +
            "and b.leavingDate > :enteringDate")
    boolean hasOverlappingBookings(Long roomId, LocalDate enteringDate, LocalDate leavingDate);

    @Query("select b.roomId from  Booking b " +
            "where b.enteringDate < :leavingDate " +
            "and b.leavingDate > :enteringDate")
    List<Long> bookedRoomsByDates(LocalDate enteringDate, LocalDate leavingDate);
}
