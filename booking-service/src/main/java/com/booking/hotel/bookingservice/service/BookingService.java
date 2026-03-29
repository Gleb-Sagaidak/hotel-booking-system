package com.booking.hotel.bookingservice.service;


import com.booking.hotel.bookingservice.dto.BookingRequestDTO;
import com.booking.hotel.bookingservice.entity.Booking;
import com.booking.hotel.bookingservice.entity.RoomInfo;
import com.booking.hotel.bookingservice.repository.BookingRepository;
import com.booking.hotel.bookingservice.repository.RoomInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomInfoRepository roomInfoRepository;
    @Transactional
    public String createBooking(BookingRequestDTO bookingRequest){
        if (bookingRequest.getEnteringDate().isAfter(bookingRequest.getLeavingDate())) {
            throw new RuntimeException("Entering date is after leaving date");//make Response Entity
        }
        if (bookingRepository.hasOverlappingBookings(bookingRequest.getRoomId(),
                bookingRequest.getEnteringDate(), bookingRequest.getLeavingDate())) {
            throw new RuntimeException("Overlapping bookings, room is not available for this date");//make responeseEntity
        }
        Booking booking = new Booking();
        booking.setRoomId(bookingRequest.getRoomId());
        booking.setClientId(bookingRequest.getClientId());
        booking.setEnteringDate(bookingRequest.getEnteringDate());
        booking.setLeavingDate(bookingRequest.getLeavingDate());
        booking.setCost(calculateTotalCost(bookingRequest));
        bookingRepository.save(booking);
        return "Booking created with ID " + booking.getBookingId();// make response Entity
    }
    private Double calculateTotalCost(BookingRequestDTO request) {
        RoomInfo roomInfo = roomInfoRepository.findById(request.getRoomId()).
                orElseThrow(() -> new RuntimeException("Room not found"));

        Double pricePerNight = roomInfo.getPricePerNight();
        long days = ChronoUnit.DAYS.between(request.getEnteringDate(), request.getLeavingDate());
        return pricePerNight * days;
    }

    public List<RoomInfo> findAvailableRooms(LocalDate enteringDate, LocalDate leavingDate) {
        return List.copyOf(roomInfoRepository.findAllByRoomIdNotIn(
                bookingRepository.bookedRoomsByDates(enteringDate, leavingDate)
        ));
    }
}
