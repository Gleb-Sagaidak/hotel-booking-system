package com.booking.hotel.bookingservice.service;


import com.booking.hotel.bookingservice.dto.BookingRequestDTO;
import com.booking.hotel.bookingservice.entity.Booking;
import com.booking.hotel.bookingservice.entity.BookingState;
import com.booking.hotel.bookingservice.entity.RoomInfo;
import com.booking.hotel.bookingservice.repository.BookingRepository;
import com.booking.hotel.bookingservice.repository.RoomInfoRepository;
import lombok.RequiredArgsConstructor;
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
            throw new RuntimeException("Entering date is after leaving date");
        }
        if (bookingRepository.hasOverlappingBookings(bookingRequest.getRoomId(),
                bookingRequest.getEnteringDate(), bookingRequest.getLeavingDate())) {
            throw new RuntimeException("Overlapping bookings, room is not available for this date");
        }
        Booking booking = new Booking();
        booking.setRoomId(bookingRequest.getRoomId());
        booking.setClientId(bookingRequest.getClientId());
        booking.setEnteringDate(bookingRequest.getEnteringDate());
        booking.setLeavingDate(bookingRequest.getLeavingDate());
        booking.setCost(calculateTotalCost(bookingRequest));
        bookingRepository.save(booking);
        return "Booking created with ID " + booking.getBookingId();
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

    public String cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new RuntimeException("Booking not found"));
        booking.setState(BookingState.CANCELLED);
        bookingRepository.save(booking);
        return "Booking cancelled with ID " + booking.getBookingId();
    }
    public String updateBooking(Long bookingId, BookingRequestDTO bookingRequest){
        Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new RuntimeException("Booking not found")
        );
        if (bookingRequest.getEnteringDate().isAfter(bookingRequest.getLeavingDate())) {
            throw new RuntimeException("Entering date is after leaving date");
        }
        boolean isOverLapping = bookingRepository.hasOverlappingBookingsExcludingSelf(bookingRequest.getRoomId(),
                bookingRequest.getEnteringDate(), bookingRequest.getLeavingDate(), bookingId);
        if (isOverLapping) {
            throw new RuntimeException("Overlapping bookings, you cannot choose these dates");
        }
        existingBooking.setLeavingDate(bookingRequest.getLeavingDate());
        existingBooking.setEnteringDate(bookingRequest.getEnteringDate());
        existingBooking.setCost(calculateTotalCost(bookingRequest));
        bookingRepository.save(existingBooking);
        return "Booking successfully updated with ID " + existingBooking.getBookingId();
    }

    public Booking getBookingDetails(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new RuntimeException("Booking not found")
        );
    }
    public List<Booking> findBookingsByClientId(Long clientId) {
        List<Booking> bookings =  bookingRepository.findBookingsByClientId(clientId);
        if (bookings.isEmpty()) {
            throw new RuntimeException("No bookings found for client ID " + clientId);
        }
        return bookings;
    }
}
