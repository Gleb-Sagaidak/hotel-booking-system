package com.booking.hotel.bookingservice.controller;

import com.booking.hotel.bookingservice.dto.BookingRequestDTO;
import com.booking.hotel.bookingservice.dto.RoomPriceEvent;
import com.booking.hotel.bookingservice.entity.RoomInfo;
import com.booking.hotel.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    @PostMapping
    public String createBooking(@Valid @RequestBody BookingRequestDTO bookingRequest){
        return bookingService.createBooking(bookingRequest);
    }

    @GetMapping("/find")
    public List<RoomInfo> getAvailableRooms(
            @RequestParam("enteringDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enteringDate,
            @RequestParam("leavingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate leavingDate) {
        return bookingService.findAvailableRooms(enteringDate, leavingDate);
    }
}
