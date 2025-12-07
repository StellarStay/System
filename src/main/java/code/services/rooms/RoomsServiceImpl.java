package code.services.rooms;

import code.model.dto.rooms.RoomRequestDTO;
import code.model.entity.rooms.RoomEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomsServiceImpl implements RoomsService{

    @Override
    public boolean insertRoom(RoomRequestDTO roomRequestDTO) {
        return false;
    }

    @Override
    public boolean updateRoom(String roomId, RoomRequestDTO roomRequestDTO) {
        return false;
    }

    @Override
    public boolean deleteRoom(String roomId) {
        return false;
    }

    @Override
    public List<RoomEntity> getAllRooms() {
        return List.of();
    }

    @Override
    public RoomRequestDTO getRoomById(String roomId) {
        return null;
    }
}
