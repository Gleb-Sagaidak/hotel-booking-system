package com.booking.hotel.bookingservice.repository;

import com.booking.hotel.bookingservice.entity.RoomInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomInfoRepository extends JpaRepository<RoomInfo, Long> {
    List<RoomInfo> findAllByRoomIdNotIn(List<Long> bookedRoomsIds);
}
