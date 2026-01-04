package code.services.rooms;

import code.model.dto.rooms.ImageRoomRequestDTO;
import code.model.entity.rooms.ImageRoomEntity;
import code.model.entity.rooms.RoomEntity;
import code.repository.rooms.ImageRoomRepository;
import code.repository.rooms.RoomRepository;
import code.services.s3.S3Service;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageRoomServiceImpl implements ImageRoomService {
    @Autowired
    private ImageRoomRepository imageRoomRepository;
    @Autowired
    private RoomsService roomsService;
    @Autowired
    private S3Service s3Service;

    int max_length_id = 8;

    private String randomId() {
        String imageId;
        do {
            imageId = RandomId.generateRoomId(max_length_id);
        } while (imageRoomRepository.findById(imageId).isPresent());
        return imageId;
    }

    @Override
    public boolean insertImageRoom(String roomId, List<MultipartFile> images) {
        try {
            RoomEntity roomEntity = roomsService.getRoomById(roomId);
            if (roomEntity == null) {
                return false;
            }
            // Bước 2: Upload ảnh lên S3 và tạo ImageRoomEntity
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    // Validate ảnh
                    if (image.isEmpty()) {
                        continue; // Bỏ qua file rỗng
                    }

                    String contentType = image.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        continue; // Bỏ qua file không phải ảnh
                    }

                    // Upload lên S3
                    String imageUrl = s3Service.uploadFile(image, "rooms");

                    // Tạo ImageRoomEntity
                    ImageRoomEntity imageRoomEntity = new ImageRoomEntity();
                    imageRoomEntity.setImageId(randomId());
                    imageRoomEntity.setImageUrl(imageUrl);
                    imageRoomEntity.setCreatedAt(LocalDateTime.now());
                    imageRoomEntity.setRoom(roomEntity);

                    // Lưu ImageRoomEntity
                    imageRoomRepository.save(imageRoomEntity);
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
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
