package code.services.rooms;

import code.model.dto.rooms.RoomRequestDTO;
import code.model.entity.rooms.CategoriesRoomEntity;
import code.model.entity.rooms.ImageRoomEntity;
import code.model.entity.rooms.RoomEntity;
import code.model.entity.users.UserEntity;
import code.repository.rooms.CategoriesRoomRepository;
import code.repository.rooms.RoomRepository;
import code.repository.users.UserRepository;
import code.services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomsServiceImpl implements RoomsService{
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryRoomService categoryRoomService;


    @Override
    public boolean insertRoom(RoomRequestDTO roomRequestDTO) {
        RoomEntity roomEntity = new RoomEntity();
        UserEntity ownerEntity = userService.getUser(roomRequestDTO.getOwnerId());
        CategoriesRoomEntity cateRoom = categoryRoomService.getCategoryRoom(roomRequestDTO.getCategoriesId());
        roomEntity.setRoomName(roomRequestDTO.getRoomName());
        roomEntity.setTitle(roomRequestDTO.getTitle());
        roomEntity.setDescription(roomRequestDTO.getDescription());
        roomEntity.setAddress(roomRequestDTO.getAddress());
        roomEntity.setPrice_per_night(roomRequestDTO.getPrice_per_night());
        roomEntity.setMax_guests(roomRequestDTO.getMax_guests());
        roomEntity.setCreatedAt(LocalDateTime.now());
        roomEntity.setStatus("PENDING");
        roomEntity.setOwner(ownerEntity);
        roomEntity.setCategory(cateRoom);
        roomRepository.save(roomEntity);
        return true;
    }

    @Override
    public boolean updateRoom(String roomId, RoomRequestDTO roomRequestDTO) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElse(null);
        if (roomEntity == null) {
            return false;
        }
        UserEntity ownerEntity = userService.getUser(roomRequestDTO.getOwnerId());
        CategoriesRoomEntity cateRoom = categoryRoomService.getCategoryRoom(roomRequestDTO.getCategoriesId());
        roomEntity.setRoomName(roomRequestDTO.getRoomName());
        roomEntity.setTitle(roomRequestDTO.getTitle());
        roomEntity.setDescription(roomRequestDTO.getDescription());
        roomEntity.setAddress(roomRequestDTO.getAddress());
        roomEntity.setPrice_per_night(roomRequestDTO.getPrice_per_night());
        roomEntity.setMax_guests(roomRequestDTO.getMax_guests());
        roomEntity.setCreatedAt(LocalDateTime.now());
        roomEntity.setStatus(roomRequestDTO.getStatus());
        roomEntity.setOwner(ownerEntity);
        roomEntity.setCategory(cateRoom);
        roomRepository.save(roomEntity);
        return true;
    }

    @Override
    public boolean deleteRoom(String roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElse(null);
        if (roomEntity == null) {
            return false;
        }
        roomRepository.delete(roomEntity);
        return true;
    }

    @Override
    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public RoomEntity getRoomById(String roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }
}
