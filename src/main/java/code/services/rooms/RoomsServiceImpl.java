package code.services.rooms;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.model.dto.rooms.req.RoomRequestDTO;
import code.model.dto.rooms.res.RoomResponseDTO;
import code.model.entity.rooms.CategoriesRoomEntity;
import code.model.entity.rooms.RoomEntity;
import code.model.entity.users.UserEntity;
import code.repository.rooms.ImageRoomRepository;
import code.repository.rooms.RoomRepository;
import code.services.s3.S3Service;
import code.services.users.UserService;
import code.util.RandomId;
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
    @Autowired
    private ImageRoomRepository imageRoomRepository;
    @Autowired
    private S3Service s3Service;

    private final int MAX_LENGTH_ROOM_ID = 10;
    private final int MAX_LENGTH_IMAGE_ID = 15;

    private String generateRoomId() {
        String randomRoomId;
        do {
            randomRoomId = RandomId.generateRoomId(MAX_LENGTH_ROOM_ID);
        } while (roomRepository.existsById(randomRoomId));
        return randomRoomId;
    }

    private String generateImageId() {
        String randomImageId;
        do {
            randomImageId = RandomId.generateRoomId(MAX_LENGTH_IMAGE_ID);
        } while (imageRoomRepository.existsById(randomImageId));
        return randomImageId;
    }

    @Override
    public boolean insertRoom(RoomRequestDTO roomRequestDTO) {
        if (roomRequestDTO == null) {
            throw new BadRequestException("RoomRequestDTO is null");
        }
        // Bước 1: Tạo RoomEntity
        RoomEntity roomEntity = new RoomEntity();
        UserEntity ownerEntity = userService.getUser(roomRequestDTO.getOwnerId());
        CategoriesRoomEntity cateRoom = categoryRoomService.getCategoryRoomByCateId(roomRequestDTO.getCategoriesId());

        roomEntity.setRoomId(generateRoomId());
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

        // Lưu RoomEntity trước
        roomRepository.save(roomEntity);

        return true;
    }

    @Override
    public boolean updateRoom(String roomId, RoomRequestDTO roomRequestDTO) {
        if (roomId == null || roomRequestDTO == null) {
            throw new BadRequestException("RoomId or RoomRequestDTO is null");
        }
        RoomEntity roomEntity = roomRepository.findById(roomId).orElse(null);
        if (roomEntity == null) {
            throw new ResourceNotFoundException("Room not found");
        }
        UserEntity ownerEntity = userService.getUser(roomRequestDTO.getOwnerId());
        CategoriesRoomEntity cateRoom = categoryRoomService.getCategoryRoomByCateId(roomRequestDTO.getCategoriesId());
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
        if (roomId == null) {
            throw new BadRequestException("RoomId is null");
        }
        RoomEntity roomEntity = roomRepository.findById(roomId).orElse(null);
        if (roomEntity == null) {
            return false;
        }
        roomEntity.setStatus("INACTIVE");
        roomRepository.save(roomEntity);
        return true;
    }

    @Override
    public List<RoomResponseDTO> getAllRooms() {
        List<RoomEntity> roomEntities = roomRepository.findAll();
        if (roomEntities.isEmpty()) {
            throw new ResourceNotFoundException("Room List not found");
        }
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
        if (roomId == null) {
            throw new BadRequestException("RoomId is null");
        }
        return roomRepository.findById(roomId).orElse(null);
    }

    @Override
    public RoomResponseDTO getRoomResponseById(String roomId) {
        if (roomId == null) {
            throw new BadRequestException("RoomId is null");
        }
        RoomEntity roomEntity = roomRepository.findById(roomId).orElse(null);
        if (roomEntity == null) {
            throw new ResourceNotFoundException("Room not found");
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
        if (minPrice == null || maxPrice == null || minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("MinPrice or MaxPrice is null or negative");
        }
        List<RoomEntity> roomEntityList = roomRepository.findAll();
        if (roomEntityList.isEmpty()){
            throw new ResourceNotFoundException("Room List not found");
        }
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
        if (maxGuests <= 0) {
            throw new BadRequestException("MaxGuests is invalid");
        }
        List<RoomEntity> roomEntityList = roomRepository.findAll();
        if (roomEntityList.isEmpty()){
            throw new ResourceNotFoundException("Room List not found");
        }
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
        if (roomEntityList.isEmpty()){
            throw new ResourceNotFoundException("Room List not found");
        }
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
        if (roomEntityList.isEmpty()){
            throw new ResourceNotFoundException("Room List not found");
        }
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
        if (planCheckInDate == null && planCheckOutDate == null){
            throw new BadRequestException("PlanCheckInDate or PlanCheckOutDate is null");
        }
        List<RoomEntity> bookedRooms = roomRepository.findAvailableRooms(planCheckInDate, planCheckOutDate);
        if (bookedRooms.isEmpty()){
            throw new ResourceNotFoundException("No available rooms found");
        }
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
