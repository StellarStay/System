package code.controller.rooms;

import code.services.rooms.ImageRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/images")
public class ImageRoomControlller {

    @Autowired
    private ImageRoomService imageRoomService;

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PostMapping(value = "/{roomId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload room images")
    public ResponseEntity<?> uploadImages(
            @PathVariable String roomId,
            @Parameter(description = "Image files", required = true)
            @RequestParam("images") List<MultipartFile> images) {
        try {
            boolean result = imageRoomService.insertImageRoom(roomId, images);
            if (result) {
                return ResponseEntity.ok("Images inserted successfully");
            }
            else {
                return ResponseEntity.status(500).body("Failed to insert images");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
