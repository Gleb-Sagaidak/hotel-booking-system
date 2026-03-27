package com.booking.hotel.bookingservice.repository;

import com.booking.hotel.bookingservice.entity.RoomInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomInfoRepository extends JpaRepository<RoomInfo, Long> {
}
