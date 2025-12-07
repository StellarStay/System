package code.services.rooms;

import code.model.dto.rooms.CategoryRoomRequestDTO;
import code.model.entity.rooms.CategoriesRoomEntity;
import code.repository.rooms.CategoriesRoomRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryRoomServiceImpl implements CategoryRoomService {
    @Autowired
    private CategoriesRoomRepository categoriesRoomRepository;
    int max_length_id = 8;

    private String randomCateId(){
        String randomCateId;
        do{
            randomCateId = RandomId.generateRoomId(max_length_id);
        }while (categoriesRoomRepository.findById(randomCateId).isPresent());
        return randomCateId;
    }
    @Override
    public boolean createCategoryRoom(CategoryRoomRequestDTO categoryRoomRequestDTO) {
        CategoriesRoomEntity categoriesRoomEntity = new CategoriesRoomEntity();
        categoriesRoomEntity.setCategoryId(randomCateId());
        categoriesRoomEntity.setCategoryName(categoryRoomRequestDTO.getCategoryName());
        categoriesRoomRepository.save(categoriesRoomEntity);
        return true;
    }

    @Override
    public boolean updateCategoryRoom(String cateRoomId, CategoryRoomRequestDTO categoryRoomRequestDTO) {
        CategoriesRoomEntity categoriesRoomEntity = categoriesRoomRepository.findById(cateRoomId).orElse(null);
        if(categoriesRoomEntity == null){
            return false;
        }
        categoriesRoomEntity.setCategoryName(categoryRoomRequestDTO.getCategoryName());
        categoriesRoomRepository.save(categoriesRoomEntity);
        return true;
    }

    @Override
    public boolean deleteCategoryRoom(String cateRoomId) {
        if (cateRoomId == null) {
            return false;
        }
        categoriesRoomRepository.deleteById(cateRoomId);
        return true;
    }

    @Override
    public CategoriesRoomEntity getCategoryRoom(String cateRoomId) {
        return categoriesRoomRepository.findById(cateRoomId).orElse(null);
    }

    @Override
    public List<CategoriesRoomEntity> getAllCategoriesRoom(String cateRoomId) {
        return categoriesRoomRepository.findAll();
    }
}
