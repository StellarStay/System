package code.services.rooms;

import code.model.dto.rooms.ImageRoomRequestDTO;
import code.model.entity.rooms.ImageRoomEntity;
import code.model.entity.rooms.RoomEntity;
import code.repository.rooms.ImageRoomRepository;
import code.repository.rooms.RoomRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageRoomServiceImpl implements ImageRoomService{
    @Autowired
    private ImageRoomRepository imageRoomRepository;
    @Autowired
    private RoomsService roomsService;

    int max_length_id = 8;
    private String randomId(){
        String imageId;
        do{
            imageId = RandomId.generateRoomId(max_length_id);
        }while(imageRoomRepository.findById(imageId).isPresent());
        return imageId;
    }

    @Override
    public boolean insertImageRoom(ImageRoomRequestDTO imageRoomRequestDTO) {
        RoomEntity roomEntity = roomsService.getRoomById(imageRoomRequestDTO.getRoomId());
        if (roomEntity == null) {
            return false;
        }
        ImageRoomEntity imageRoomEntity = new ImageRoomEntity();
        imageRoomEntity.setImageId(randomId());
        imageRoomEntity.setCreatedAt(LocalDateTime.now());
        imageRoomEntity.setImageUrl(imageRoomRequestDTO.getImageUrl());
        imageRoomEntity.setRoom(roomEntity);
        imageRoomRepository.save(imageRoomEntity);
        return true;
    }

    @Override
    public boolean updateImageRoom(String imageRoomId, ImageRoomRequestDTO imageRoomRequestDTO) {
        ImageRoomEntity imageRoomEntity = imageRoomRepository.findById(imageRoomId).orElse(null);
        if (imageRoomEntity == null) {
            return false;
        }
        imageRoomEntity.setImageUrl(imageRoomRequestDTO.getImageUrl());
        imageRoomEntity.setRoom(roomsService.getRoomById(imageRoomRequestDTO.getRoomId()));
        imageRoomRepository.save(imageRoomEntity);
        return true;
    }

    @Override
    public boolean deleteImageRoom(String imageRoomId) {
        ImageRoomEntity imageRoomEntity = imageRoomRepository.findById(imageRoomId).orElse(null);
        if (imageRoomEntity == null) {
            return false;
        }
        imageRoomRepository.delete(imageRoomEntity);
        return true;
    }

    @Override
    public List<ImageRoomEntity> getAllImageRoom() {
        return imageRoomRepository.findAll();
    }

    @Override
    public ImageRoomEntity getImageRoomById(String imageRoomId) {
        return  imageRoomRepository.findById(imageRoomId).orElse(null);
    }
}
