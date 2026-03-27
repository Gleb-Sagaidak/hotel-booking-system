package com.booking.hotel.bookingservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@Data
@AllArgsConstructor
public class BookingRequestDTO {
    @NotNull
    private long roomId;
    @NotNull
    private long clientId;
    @FutureOrPresent
    private LocalDate enteringDate;
    @Future
    private LocalDate leavingDate;
}
