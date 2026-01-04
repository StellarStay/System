package code.services.rooms;

import code.model.dto.rooms.CategoryRoomRequestDTO;
import code.model.dto.rooms.CategoryRoomResponseDTO;
import code.model.entity.rooms.CategoriesRoomEntity;
import code.repository.rooms.CategoriesRoomRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public CategoriesRoomEntity getCategoryRoomByCateId(String cateRoomId) {
        return categoriesRoomRepository.findById(cateRoomId).orElse(null);
    }

    @Override
    public CategoryRoomResponseDTO getCategoryRoomResponseDTOByCateId(String cateRoomId) {
        CategoriesRoomEntity categoriesRoomEntity = categoriesRoomRepository.findById(cateRoomId).orElse(null);
        if(categoriesRoomEntity == null){
            return null;
        }
        CategoryRoomResponseDTO categoryRoomResponseDTO = new CategoryRoomResponseDTO();
        categoryRoomResponseDTO.setCategoryName(categoriesRoomEntity.getCategoryName());
        return categoryRoomResponseDTO;
    }

    @Override
    public List<CategoryRoomResponseDTO> getAllCategoriesRoom(String cateRoomId) {
        List<CategoriesRoomEntity> categoriesRoomEntities = categoriesRoomRepository.findAll();
        List<CategoryRoomResponseDTO> categoryRoomResponseDTOList = new ArrayList<>();

        for (CategoriesRoomEntity categoriesRoomEntity : categoriesRoomEntities) {
            CategoryRoomResponseDTO categoryRoomResponseDTO = new CategoryRoomResponseDTO();
            categoryRoomResponseDTO.setCategoryName(categoriesRoomEntity.getCategoryName());
            categoryRoomResponseDTOList.add(categoryRoomResponseDTO);
        }
        return categoryRoomResponseDTOList;
    }
}
