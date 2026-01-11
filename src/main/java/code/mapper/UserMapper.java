package code.mapper;

import code.model.dto.users.req.UserRequestDTO;
import code.model.dto.users.res.UserResponseDTO;
import code.model.entity.users.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    // Convert UserRequestDTO to UserEntity
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "chatMessages", ignore = true)
    @Mapping(target = "receivedMessages", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "reviewsRated", ignore = true)
    @Mapping(target = "reviewsReplied", ignore = true)
    UserEntity toEntity(UserRequestDTO userRequestDTO);
    
    // Convert UserEntity to UserResponseDTO
    @Mapping(source = "role.roleId", target = "roleId")
    UserResponseDTO toResponseDTO(UserEntity userEntity);
    
    // Update UserEntity from UserRequestDTO (for update operations)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "chatMessages", ignore = true)
    @Mapping(target = "receivedMessages", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "reviewsRated", ignore = true)
    @Mapping(target = "reviewsReplied", ignore = true)
    void updateEntityFromDTO(UserRequestDTO userRequestDTO, @MappingTarget UserEntity userEntity);
}
