package com.booking.hotel.roomservice.repository;

import com.booking.hotel.roomservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByNameContaining(String name);
}
