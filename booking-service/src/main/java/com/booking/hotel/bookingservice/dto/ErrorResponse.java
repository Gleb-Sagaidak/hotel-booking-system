package com.booking.hotel.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private LocalDateTime timeStamp;
    private String error;
    private String message;
}
