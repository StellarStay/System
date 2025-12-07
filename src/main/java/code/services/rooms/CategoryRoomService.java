package code.services.rooms;

import code.model.dto.rooms.CategoryRoomRequestDTO;
import code.model.entity.rooms.CategoriesRoomEntity;

import java.util.List;

public interface CategoryRoomService {
    boolean createCategoryRoom(CategoryRoomRequestDTO categoryRoomRequestDTO);
    boolean updateCategoryRoom(String cateRoomId, CategoryRoomRequestDTO categoryRoomRequestDTO);
    boolean deleteCategoryRoom(String cateRoomId);
    CategoriesRoomEntity getCategoryRoom(String cateRoomId);
    List<CategoriesRoomEntity> getAllCategoriesRoom(String cateRoomId);
}
