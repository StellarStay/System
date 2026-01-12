package code.mapper;

import code.model.dto.rooms.res.ImageRoomResponseDTO;
import code.model.entity.rooms.ImageRoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {
    ImageRoomResponseDTO toImageRoomResponse(ImageRoomEntity imageRoomEntity);
}
