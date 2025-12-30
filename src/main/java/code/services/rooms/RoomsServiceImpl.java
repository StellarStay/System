package code.services.rooms;

import code.model.dto.rooms.RoomRequestDTO;
import code.model.dto.rooms.RoomResponseDTO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<RoomResponseDTO> getAllRooms() {
        List<RoomEntity> roomEntities = roomRepository.findAll();
        List<RoomResponseDTO> roomResponseDTOList = new ArrayList<>();
        for (RoomEntity room : roomEntities) {
            RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
            roomResponseDTO.setRoomId(room.getRoomId());
            roomResponseDTO.setRoomName(room.getRoomName());
            roomResponseDTO.setTitle(room.getTitle());
            roomResponseDTO.setDescription(room.getDescription());
            roomResponseDTO.setAddress(room.getAddress());
            roomResponseDTO.setPrice_per_night(room.getPrice_per_night());
            roomResponseDTO.setMax_guests(room.getMax_guests());
            roomResponseDTO.setStatus(room.getStatus());
            roomResponseDTO.setCategoryName(room.getCategory().getCategoryName());
            roomResponseDTO.setOwnerName(room.getOwner().getFirstName() + " " + room.getOwner().getLastName());
            roomResponseDTOList.add(roomResponseDTO);
        }
        return  roomResponseDTOList;
    }

    @Override
    public RoomEntity getRoomById(String roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    @Override
    public RoomResponseDTO getRoomResponseById(String roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElse(null);
        if (roomEntity == null) {
            return null;
        }
        RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
        roomResponseDTO.setRoomId(roomEntity.getRoomId());
        roomResponseDTO.setRoomName(roomEntity.getRoomName());
        roomResponseDTO.setTitle(roomEntity.getTitle());
        roomResponseDTO.setDescription(roomEntity.getDescription());
        roomResponseDTO.setAddress(roomEntity.getAddress());
        roomResponseDTO.setPrice_per_night(roomEntity.getPrice_per_night());
        roomResponseDTO.setMax_guests(roomEntity.getMax_guests());
        roomResponseDTO.setStatus(roomEntity.getStatus());
        roomResponseDTO.setCategoryName(roomEntity.getCategory().getCategoryName());
        roomResponseDTO.setOwnerName(roomEntity.getOwner().getFirstName() + " " + roomEntity.getOwner().getLastName());
        return roomResponseDTO;
    }

    @Override
    public List<RoomResponseDTO> getRoomByPricePerNight(BigDecimal minPrice, BigDecimal maxPrice) {
        List<RoomEntity> roomEntityList = roomRepository.findAll();
        List<RoomResponseDTO> roomResponseDTOList = new ArrayList<>();
        for (RoomEntity room : roomEntityList) {
            if (room.getPrice_per_night().compareTo(minPrice) >= 0 && room.getPrice_per_night().compareTo(maxPrice) <= 0) {
                RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
                roomResponseDTO.setRoomId(room.getRoomId());
                roomResponseDTO.setRoomName(room.getRoomName());
                roomResponseDTO.setTitle(room.getTitle());
                roomResponseDTO.setDescription(room.getDescription());
                roomResponseDTO.setAddress(room.getAddress());
                roomResponseDTO.setPrice_per_night(room.getPrice_per_night());
                roomResponseDTO.setMax_guests(room.getMax_guests());
                roomResponseDTO.setStatus(room.getStatus());
                roomResponseDTO.setCategoryName(room.getCategory().getCategoryName());
                roomResponseDTO.setOwnerName(room.getOwner().getFirstName() + " " + room.getOwner().getLastName());
                roomResponseDTOList.add(roomResponseDTO);
            }
        }
        return roomResponseDTOList;
    }

    @Override
    public List<RoomResponseDTO> getRoomByMaxGuests(int maxGuests) {
        List<RoomEntity> roomEntityList = roomRepository.findAll();
        List<RoomResponseDTO> roomResponseDTOList = new ArrayList<>();
        for (RoomEntity room : roomEntityList) {
            if (room.getMax_guests() == maxGuests) {
                RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
                roomResponseDTO.setRoomId(room.getRoomId());
                roomResponseDTO.setRoomName(room.getRoomName());
                roomResponseDTO.setTitle(room.getTitle());
                roomResponseDTO.setDescription(room.getDescription());
                roomResponseDTO.setAddress(room.getAddress());
                roomResponseDTO.setPrice_per_night(room.getPrice_per_night());
                roomResponseDTO.setMax_guests(room.getMax_guests());
                roomResponseDTO.setStatus(room.getStatus());
                roomResponseDTO.setCategoryName(room.getCategory().getCategoryName());
                roomResponseDTO.setOwnerName(room.getOwner().getFirstName() + " " + room.getOwner().getLastName());
                roomResponseDTOList.add(roomResponseDTO);
            }
        }
        return roomResponseDTOList;
    }

    @Override
    public List<RoomResponseDTO> getRoomByAddress(String address) {
        List<RoomEntity> roomEntityList = roomRepository.findAll();
        List<RoomResponseDTO> roomResponseDTOList = new ArrayList<>();
        for (RoomEntity room : roomEntityList) {
            if (room.getAddress().equals(address)) {
                RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
                roomResponseDTO.setRoomId(room.getRoomId());
                roomResponseDTO.setRoomName(room.getRoomName());
                roomResponseDTO.setTitle(room.getTitle());
                roomResponseDTO.setDescription(room.getDescription());
                roomResponseDTO.setAddress(room.getAddress());
                roomResponseDTO.setPrice_per_night(room.getPrice_per_night());
                roomResponseDTO.setMax_guests(room.getMax_guests());
                roomResponseDTO.setStatus(room.getStatus());
                roomResponseDTO.setCategoryName(room.getCategory().getCategoryName());
                roomResponseDTO.setOwnerName(room.getOwner().getFirstName() + " " + room.getOwner().getLastName());
                roomResponseDTOList.add(roomResponseDTO);
            }
        }
        return roomResponseDTOList;
    }

    @Override
    public List<RoomResponseDTO> getRoomByCategory(String categoryId) {
        List<RoomEntity> roomEntityList = roomRepository.findAll();
        List<RoomResponseDTO> roomResponseDTOList = new ArrayList<>();
        for (RoomEntity room : roomEntityList) {
            if (room.getCategory().getCategoryId().equals(categoryId)) {
                RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
                roomResponseDTO.setRoomId(room.getRoomId());
                roomResponseDTO.setRoomName(room.getRoomName());
                roomResponseDTO.setTitle(room.getTitle());
                roomResponseDTO.setDescription(room.getDescription());
                roomResponseDTO.setAddress(room.getAddress());
                roomResponseDTO.setPrice_per_night(room.getPrice_per_night());
                roomResponseDTO.setMax_guests(room.getMax_guests());
                roomResponseDTO.setStatus(room.getStatus());
                roomResponseDTO.setCategoryName(room.getCategory().getCategoryName());
                roomResponseDTO.setOwnerName(room.getOwner().getFirstName() + " " + room.getOwner().getLastName());
                roomResponseDTOList.add(roomResponseDTO);
            }
        }
        return roomResponseDTOList;
    }

    @Override
    public List<RoomResponseDTO> getRoomByDateAvailability(LocalDateTime planCheckInDate, LocalDateTime planCheckOutDate) {
        List<RoomEntity> bookedRooms = roomRepository.findAvailableRooms(planCheckInDate, planCheckOutDate);
        List<RoomResponseDTO> roomResponseDTOList = new ArrayList<>();
        for (RoomEntity room : bookedRooms) {
            RoomResponseDTO roomResponseDTO = new RoomResponseDTO();

            roomResponseDTO.setRoomId(room.getRoomId());
            roomResponseDTO.setRoomName(room.getRoomName());
            roomResponseDTO.setTitle(room.getTitle());
            roomResponseDTO.setDescription(room.getDescription());
            roomResponseDTO.setAddress(room.getAddress());
            roomResponseDTO.setPrice_per_night(room.getPrice_per_night());
            roomResponseDTO.setMax_guests(room.getMax_guests());
            roomResponseDTO.setStatus(room.getStatus());
            roomResponseDTO.setCategoryName(room.getCategory().getCategoryName());
            roomResponseDTO.setOwnerName(room.getOwner().getFirstName() + " " + room.getOwner().getLastName());

            roomResponseDTOList.add(roomResponseDTO);
        }
        return  roomResponseDTOList;
    }
}
