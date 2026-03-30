package com.booking.hotel.bookingservice.controller;

import com.booking.hotel.bookingservice.dto.BookingRequestDTO;
import com.booking.hotel.bookingservice.entity.Booking;
import com.booking.hotel.bookingservice.entity.RoomInfo;
import com.booking.hotel.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequest){
        String response = bookingService.createBooking(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/find")
    public List<RoomInfo> getAvailableRooms(
            @RequestParam("enteringDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enteringDate,
            @RequestParam("leavingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate leavingDate) {
        return bookingService.findAvailableRooms(enteringDate, leavingDate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        String result = bookingService.cancelBooking(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody BookingRequestDTO request){
        String result = bookingService.updateBooking(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingDetails(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookingDetails(id));
    }
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Booking>> getClientBookings(@PathVariable Long clientId){
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.findBookingsByClientId(clientId));
    }
}
