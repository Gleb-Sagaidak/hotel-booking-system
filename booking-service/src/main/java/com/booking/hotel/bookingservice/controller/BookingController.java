package com.booking.hotel.bookingservice.controller;

import com.booking.hotel.bookingservice.dto.BookingRequestDTO;
import com.booking.hotel.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    @PostMapping
    public String createBooking(@Valid @RequestBody BookingRequestDTO bookingRequest){
        return bookingService.createBooking(bookingRequest);
    }
}
