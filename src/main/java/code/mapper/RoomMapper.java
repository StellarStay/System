package code.mapper;

import code.model.dto.rooms.res.RoomResponseDTO;
import code.model.entity.rooms.RoomEntity;
import code.model.entity.users.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)

public interface RoomMapper {

    @Mapping(source = "category.categoryName" , target = "categoryName")
    @Mapping(target = "ownerName", expression = "java(getFullNameOwner(roomEntity.getOwner()))")
    RoomResponseDTO toRoomResponseDTO(RoomEntity roomEntity);

    // Tạo một default func để chuyển đổi owner thành owner lastName + " " + owner firstName
    default String getFullNameOwner(UserEntity owner) {
        if (owner == null) {
            return null;
        }
        return owner.getFirstName() + " " + owner.getLastName();
    }
}
