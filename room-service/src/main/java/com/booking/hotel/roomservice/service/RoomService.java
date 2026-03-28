package com.booking.hotel.roomservice.service;

import com.booking.hotel.roomservice.dto.RoomPriceEvent;
import com.booking.hotel.roomservice.entity.Room;
import com.booking.hotel.roomservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final KafkaTemplate<String, RoomPriceEvent> kafkaTemplate;


    public void createRoom(Room room) {
        Room savedRoom = roomRepository.save(room);
        sendPriceRoomEvent(savedRoom);
    }
    public void updateRoom(Long roomId, Double newPrice) {
        roomRepository.findById(roomId).ifPresent(r -> {
            r.setPrice(newPrice);
            roomRepository.save(r);
            sendPriceRoomEvent(r);
        });

    }
    public List<Room> getAllRooms() {
        return List.copyOf(roomRepository.findAll());
    }
    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }
    private void sendPriceRoomEvent(Room room) {
        RoomPriceEvent event = new RoomPriceEvent(room.getId(), room.getPrice());
        kafkaTemplate.send("room-prices", event);
        System.out.println("JSON sent to Kafka: " + event);

//        try {
//            RoomPriceEvent event = new RoomPriceEvent(room.getId(), room.getPrice());
//
//            String jsonEvent = objectMapper.writeValueAsString(event);
//
//            kafkaTemplate.send("room-prices", jsonEvent);
//            System.out.println("JSON sent to Kafka: " + jsonEvent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    public List<Room> findRoomByName(String roomName) {
        return roomRepository.findByNameContaining(roomName);
    }

}
