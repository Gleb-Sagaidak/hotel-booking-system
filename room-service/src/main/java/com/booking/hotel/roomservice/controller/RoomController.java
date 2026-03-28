package com.booking.hotel.roomservice.controller;

import com.booking.hotel.roomservice.entity.Room;
import com.booking.hotel.roomservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    @PostMapping
    public void createRoom(@RequestBody Room room) {
        roomService.createRoom(room);
    }
    @PutMapping("/{id}/price")
    public void updateRoom(@PathVariable Long id, @RequestParam Double price) {
        roomService.updateRoom(id, price);
    }
    @GetMapping
    public List<Room> getAllRooms(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return roomService.findRoomByName(name);
        }
        return roomService.getAllRooms();
    }

}
