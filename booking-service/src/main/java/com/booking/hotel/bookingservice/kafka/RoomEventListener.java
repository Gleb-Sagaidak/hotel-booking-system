package com.booking.hotel.bookingservice.kafka;

import com.booking.hotel.bookingservice.dto.RoomPriceEvent;
import com.booking.hotel.bookingservice.entity.RoomInfo;
import com.booking.hotel.bookingservice.repository.RoomInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomEventListener {
    private final RoomInfoRepository roomInfoRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "room-prices", groupId = "booking-groupv2")
    public void consumeRoomPrice(String message) {
        String cleanMessage = message.trim().replaceAll("^[^\\{]*", "");
        try {
            log.info("Received kafka message: " + message);
            if (cleanMessage.isEmpty()) return;
            RoomPriceEvent event = objectMapper.readValue(message, RoomPriceEvent.class);

            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setRoomId(event.getRoomId());
            roomInfo.setPricePerNight(event.getPrice());

            roomInfoRepository.save(roomInfo);
            log.info("Room info update for " + event.getRoomId());
        }catch (Exception e) {
            log.error("Error parsing json: ",e);
        }
    }
}
