package code.services.rooms;


import code.model.dto.rooms.RoomRequestDTO;
import code.model.dto.rooms.RoomResponseDTO;
import code.model.entity.rooms.RoomEntity;

import java.math.BigDecimal;
import java.util.List;

public interface RoomsService {
    boolean insertRoom(RoomRequestDTO roomRequestDTO);
    boolean updateRoom(String roomId, RoomRequestDTO roomRequestDTO);
    boolean deleteRoom(String roomId);
    List<RoomResponseDTO> getAllRooms();
    RoomEntity getRoomById(String roomId);
    RoomResponseDTO getRoomResponseById(String roomId);
    List<RoomResponseDTO> getRoomByPricePerNight(BigDecimal minPrice, BigDecimal maxPrice);
    List<RoomResponseDTO> getRoomByMaxGuests(int maxGuests);
    List<RoomResponseDTO> getRoomByAddress(String address);
    List<RoomResponseDTO> getRoomByCategory(String categoryId);


}
