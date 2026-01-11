package code.controller.category;

import code.model.dto.rooms.req.CategoryRoomRequestDTO;
import code.model.dto.rooms.res.CategoryRoomResponseDTO;
import code.services.rooms.CategoryRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryRoomService categoryRoomService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create_category")
    public ResponseEntity<String> createCategory(@RequestBody CategoryRoomRequestDTO categoryRoomRequestDTO){
        boolean inserted = categoryRoomService.createCategoryRoom(categoryRoomRequestDTO);
        if (inserted) {
            return ResponseEntity.ok("Category created successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to create category");
        }
    }
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/update_category/{cateRoomId}")
    public ResponseEntity<String> updateCategory(@PathVariable String cateRoomId, @RequestBody CategoryRoomRequestDTO categoryRoomRequestDTO) {
        boolean updated = categoryRoomService.updateCategoryRoom(cateRoomId, categoryRoomRequestDTO);
        if (updated) {
            return ResponseEntity.ok("Category updated successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to update category");
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete_category/{cateRoomId}")
    public ResponseEntity<String> deleteCategory(@PathVariable String cateRoomId) {
        boolean deleted = categoryRoomService.deleteCategoryRoom(cateRoomId);
        if (deleted) {
            return ResponseEntity.ok("Category deleted successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to delete category");
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_category/{cateRoomId}")
    public ResponseEntity<CategoryRoomResponseDTO> getCategory(@PathVariable String cateRoomId) {
        CategoryRoomResponseDTO categoryRoomResponseDTO = categoryRoomService.getCategoryRoomResponseDTOByCateId(cateRoomId);
        if (categoryRoomResponseDTO != null) {
            return ResponseEntity.ok(categoryRoomResponseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get_all_categories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryRoomService.getAllCategoriesRoom(null));
    }

}
