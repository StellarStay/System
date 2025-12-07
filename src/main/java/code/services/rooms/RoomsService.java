package code.services.rooms;


import code.model.dto.rooms.RoomRequestDTO;
import code.model.entity.rooms.RoomEntity;

import java.util.List;

public interface RoomsService {
    boolean insertRoom(RoomRequestDTO roomRequestDTO);
    boolean updateRoom(String roomId, RoomRequestDTO roomRequestDTO);
    boolean deleteRoom(String roomId);
    List<RoomEntity> getAllRooms();
    RoomRequestDTO getRoomById(String roomId);

}
