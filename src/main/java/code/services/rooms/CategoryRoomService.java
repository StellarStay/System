package code.services.rooms;

import code.model.dto.rooms.req.CategoryRoomRequestDTO;
import code.model.dto.rooms.res.CategoryRoomResponseDTO;
import code.model.entity.rooms.CategoriesRoomEntity;

import java.util.List;

public interface CategoryRoomService {
    boolean createCategoryRoom(CategoryRoomRequestDTO categoryRoomRequestDTO);
    boolean updateCategoryRoom(String cateRoomId, CategoryRoomRequestDTO categoryRoomRequestDTO);
    boolean deleteCategoryRoom(String cateRoomId);
    CategoriesRoomEntity getCategoryRoomByCateId(String cateRoomId);
    CategoryRoomResponseDTO getCategoryRoomResponseDTOByCateId(String cateRoomId);
    List<CategoryRoomResponseDTO> getAllCategoriesRoom(String cateRoomId);
}
