package com.booking.hotel.bookingservice.service;


import com.booking.hotel.bookingservice.dto.BookingRequestDTO;
import com.booking.hotel.bookingservice.entity.Booking;
import com.booking.hotel.bookingservice.entity.RoomInfo;
import com.booking.hotel.bookingservice.repository.BookingRepository;
import com.booking.hotel.bookingservice.repository.RoomInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomInfoRepository roomInfoRepository;
    @Transactional
    public String createBooking(BookingRequestDTO bookingRequest){
        if (bookingRequest.getEnteringDate().isAfter(bookingRequest.getLeavingDate())) {
            throw new RuntimeException("Entering date is after leaving date");
        }
        RoomInfo roomInfo = roomInfoRepository.findById(bookingRequest.getRoomId()).orElse(null);
        if (roomInfo == null) {
            throw new RuntimeException("Room not found");
        }
        Double pricePerNight = roomInfo.getPricePerNight();
        Booking booking = new Booking();
        booking.setRoomId(bookingRequest.getRoomId());
        booking.setClientId(bookingRequest.getClientId());
        booking.setEnteringDate(bookingRequest.getEnteringDate());
        booking.setLeavingDate(bookingRequest.getLeavingDate());
        long days = ChronoUnit.DAYS.between(booking.getEnteringDate(), booking.getLeavingDate());
        Double totalCost = pricePerNight * days;
        booking.setCost(totalCost);
        bookingRepository.save(booking);
        return "Booking created with ID " + booking.getBookingId();
    }
}
