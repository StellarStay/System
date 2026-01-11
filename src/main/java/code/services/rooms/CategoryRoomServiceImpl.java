package code.services.rooms;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.model.dto.rooms.req.CategoryRoomRequestDTO;
import code.model.dto.rooms.res.CategoryRoomResponseDTO;
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
        if (categoryRoomRequestDTO == null) {
            throw new ResourceNotFoundException("Category Room Request is null");
        }
        CategoriesRoomEntity categoriesRoomEntity = new CategoriesRoomEntity();
        categoriesRoomEntity.setCategoryId(randomCateId());
        categoriesRoomEntity.setCategoryName(categoryRoomRequestDTO.getCategoryName());
        categoriesRoomRepository.save(categoriesRoomEntity);
        return true;
    }

    @Override
    public boolean updateCategoryRoom(String cateRoomId, CategoryRoomRequestDTO categoryRoomRequestDTO) {
        if (categoryRoomRequestDTO == null || cateRoomId == null) {
            throw new BadRequestException("Category Room Request or Category Id is null");
        }
        CategoriesRoomEntity categoriesRoomEntity = categoriesRoomRepository.findById(cateRoomId).orElse(null);
        if(categoriesRoomEntity == null){
            throw new ResourceNotFoundException("Category Room not found");
        }
        categoriesRoomEntity.setCategoryName(categoryRoomRequestDTO.getCategoryName());
        categoriesRoomRepository.save(categoriesRoomEntity);
        return true;
    }

    @Override
    public boolean deleteCategoryRoom(String cateRoomId) {
        if (cateRoomId == null) {
            throw new BadRequestException("Category Id is null");
        }
        categoriesRoomRepository.deleteById(cateRoomId);
        return true;
    }

    @Override
    public CategoriesRoomEntity getCategoryRoomByCateId(String cateRoomId) {
        if (cateRoomId == null) {
            throw new BadRequestException("Category Id is null");
        }
        return categoriesRoomRepository.findById(cateRoomId).orElse(null);
    }

    @Override
    public CategoryRoomResponseDTO getCategoryRoomResponseDTOByCateId(String cateRoomId) {
        if (cateRoomId == null) {
            throw new BadRequestException("Category Id is null");
        }
        CategoriesRoomEntity categoriesRoomEntity = categoriesRoomRepository.findById(cateRoomId).orElse(null);
        if(categoriesRoomEntity == null){
            throw new ResourceNotFoundException("Category Room not found");
        }
        CategoryRoomResponseDTO categoryRoomResponseDTO = new CategoryRoomResponseDTO();
        categoryRoomResponseDTO.setCategoryName(categoriesRoomEntity.getCategoryName());
        return categoryRoomResponseDTO;
    }

    @Override
    public List<CategoryRoomResponseDTO> getAllCategoriesRoom(String cateRoomId) {
        if (cateRoomId == null){
            throw new BadRequestException("Category Id is null");
        }
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
