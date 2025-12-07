package code.services.rooms;

import code.model.dto.rooms.ImageRoomRequestDTO;
import code.model.entity.rooms.ImageRoomEntity;

import java.util.List;

public interface ImageRoomService {
    boolean insertImageRoom(ImageRoomRequestDTO imageRoomRequestDTO);
    boolean updateImageRoom(String imageRoomId, ImageRoomRequestDTO imageRoomRequestDTO);
    boolean deleteImageRoom(String imageRoomId);
    List<ImageRoomEntity> getAllImageRoom();
    ImageRoomEntity getImageRoomById(String imageRoomId);
}
