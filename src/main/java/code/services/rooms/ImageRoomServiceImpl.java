package code.services.rooms;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.mapper.ImageMapper;
import code.model.dto.rooms.req.ImageRoomRequestDTO;
import code.model.dto.rooms.res.ImageRoomResponseDTO;
import code.model.entity.rooms.ImageRoomEntity;
import code.model.entity.rooms.RoomEntity;
import code.repository.rooms.ImageRoomRepository;
import code.services.s3.S3Service;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageRoomServiceImpl implements ImageRoomService {
    @Autowired
    private ImageRoomRepository imageRoomRepository;
    @Autowired
    private RoomsService roomsService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private ImageMapper imageMapper;

    int max_length_id = 8;

    private String randomId() {
        String imageId;
        do {
            imageId = RandomId.generateRoomId(max_length_id);
        } while (imageRoomRepository.findById(imageId).isPresent());
        return imageId;
    }

    @Override
    public void insertImageRoom(String roomId, List<MultipartFile> images) {
        try {
            if (roomId == null) {
                throw new BadRequestException("RoomId is null");
            }
            RoomEntity roomEntity = roomsService.getRoomById(roomId);
            if (roomEntity == null) {
                throw new BadRequestException("Room is not found");
            }

            // Kiểm tra room đã có ảnh chưa
            boolean hasExistingImages = !imageRoomRepository.findByRoom_RoomId(roomId).isEmpty();

            // Bước 2: Upload ảnh lên S3 và tạo ImageRoomEntity
            if (images != null && !images.isEmpty()) {

                boolean isFirstImage = !hasExistingImages;

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
                    imageRoomEntity.setIsThumbnail(isFirstImage);

                    // Lưu ImageRoomEntity
                    imageRoomRepository.save(imageRoomEntity);

                    isFirstImage = false;
                }
            }
        }
        catch (Exception e) {
            throw new ResourceNotFoundException("Image Room Not Found");
        }
    }


    @Override
    public boolean updateImageRoom(String imageRoomId, ImageRoomRequestDTO imageRoomRequestDTO) {
        if (imageRoomId == null){
            throw new BadRequestException("RoomId is null");
        }
        ImageRoomEntity imageRoomEntity = imageRoomRepository.findById(imageRoomId).orElse(null);
        if (imageRoomEntity == null) {
            throw new ResourceNotFoundException("Image Room is not found");
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
        if (imageRoomId == null){
            throw new BadRequestException("RoomId is null");
        }
        return imageRoomRepository.findById(imageRoomId).orElse(null);
    }

    @Override
    public List<ImageRoomResponseDTO> getAllImageRoomsByRoomId(String roomId) {
        if (roomId == null){
            throw new BadRequestException("RoomId is null");
        }
        List<ImageRoomEntity> imageRoomEntities = imageRoomRepository.findByRoom_RoomId(roomId);
        if (imageRoomEntities.isEmpty()) {
            throw new ResourceNotFoundException("Image Room Not Found");
        }
        List<ImageRoomResponseDTO> imageRoomResponseDTOList = new ArrayList<>();
        for (ImageRoomEntity imageRoomEntity : imageRoomEntities) {
            ImageRoomResponseDTO dto = imageMapper.toImageRoomResponse(imageRoomEntity);
            imageRoomResponseDTOList.add(dto);
        }
        return imageRoomResponseDTOList;
    }

    @Override
    public ImageRoomResponseDTO getThumbnailImageByRoomId(String roomId) {
        if (roomId == null){
            throw new BadRequestException("RoomId is null");
        }
        ImageRoomEntity thumbnailImage = imageRoomRepository.findThumbnailOfRoom(roomId);
        if (thumbnailImage == null) {
            throw new ResourceNotFoundException("Image Room Not Found");
        }
        return imageMapper.toImageRoomResponse(thumbnailImage);
    }
}
