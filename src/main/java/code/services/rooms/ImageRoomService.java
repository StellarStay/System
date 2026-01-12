package code.services.rooms;

import code.model.dto.rooms.req.ImageRoomRequestDTO;
import code.model.dto.rooms.res.ImageRoomResponseDTO;
import code.model.entity.rooms.ImageRoomEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageRoomService {
    void insertImageRoom(String roomId, List<MultipartFile> images);
    boolean updateImageRoom(String imageRoomId, ImageRoomRequestDTO imageRoomRequestDTO);
    boolean deleteImageRoom(String imageRoomId);
    List<ImageRoomEntity> getAllImageRoom();
    ImageRoomEntity getImageRoomById(String imageRoomId);
    List<ImageRoomResponseDTO> getAllImageRoomsByRoomId(String roomId);
    ImageRoomResponseDTO getThumbnailImageByRoomId(String roomId);
}
