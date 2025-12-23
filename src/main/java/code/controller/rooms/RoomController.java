package code.controller.rooms;

import code.model.dto.rooms.RoomRequestDTO;
import code.model.dto.rooms.RoomResponseDTO;
import code.model.entity.rooms.RoomEntity;
import code.services.rooms.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomsService roomsService;

    @PostMapping("/insertRoom")
    public ResponseEntity<String> insertRoom(@RequestBody RoomRequestDTO roomRequestDTO){
        boolean result = roomsService.insertRoom(roomRequestDTO);
        if(result){
            return ResponseEntity.ok("Room inserted successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to insert room");
        }
    }

    @PostMapping("/updateRoom/{roomId}")
    public ResponseEntity<String> updateRoom(@PathVariable String roomId, @RequestBody RoomRequestDTO roomRequestDTO){
        boolean result = roomsService.updateRoom(roomId, roomRequestDTO);
        if(result){
            return ResponseEntity.ok("Room updated successfully");
        } else {
            return ResponseEntity.status(404).body("Room not found");
        }
    }

    @GetMapping("/get/getAllRooms")
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() {
        List<RoomResponseDTO> roomResponseDTOS = roomsService.getAllRooms();
        return ResponseEntity.ok(roomResponseDTOS);
    }

    @GetMapping("/get/getByRoomId/{roomId}")
    public ResponseEntity<RoomResponseDTO> getByRoomId(@PathVariable String roomId){
        RoomResponseDTO room = roomsService.getRoomResponseById(roomId);
        if(room != null){
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }


    @GetMapping("/get/getByMaxGuests")
    public ResponseEntity<?> getByMaxGuests(@RequestParam int maxGuests){
        try {
            return ResponseEntity.ok(roomsService.getRoomByMaxGuests(maxGuests));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid maxGuests parameter");
        }
    }

    @GetMapping("/get/getByAddress")
    public ResponseEntity<List<RoomResponseDTO>> getByAddress(@RequestParam String address){
        List<RoomResponseDTO> rooms = roomsService.getRoomByAddress(address);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/get/getByCategory/{categoryId}")
    public ResponseEntity<List<RoomResponseDTO>> getByCategory(@PathVariable String categoryId) {
        List<RoomResponseDTO> rooms = roomsService.getRoomByCategory(categoryId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/get/getByPricePerNight")
    public ResponseEntity<List<RoomResponseDTO>> getByPricePerNight(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice){
        List<RoomResponseDTO> rooms = roomsService.getRoomByPricePerNight(minPrice, maxPrice);
        return  ResponseEntity.ok(rooms);
    }

}
