package code.controller.rooms;

import code.model.dto.rooms.DeviceOfRoomRequestDTO;
import code.model.dto.rooms.DeviceOfRoomUpdateDTO;
import code.services.rooms.DeviceOfRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms/devices")
public class DeviceOfRoomController {
    @Autowired
    private DeviceOfRoomService deviceOfRoomService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/insert_device_to_room")
    public ResponseEntity<Boolean> insertDeviceToRoom(@RequestBody DeviceOfRoomRequestDTO deviceOfRoomRequestDTO) {
        Boolean result = deviceOfRoomService.insertDeviceToRoom(deviceOfRoomRequestDTO);
        return ResponseEntity.ok(result);
    }

    // Update thì chỉ cần update lại trạng thái còn sử dụng hoặc không của device trong room
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/update_device_of_room/{deviceOfRoomId}")
    public ResponseEntity<Boolean> updateDeviceOfRoom(@PathVariable String deviceOfRoomId, @RequestBody DeviceOfRoomUpdateDTO deviceOfRoomRequestDTO) {
        boolean result = deviceOfRoomService.updateDeviceOfRoom(deviceOfRoomId, deviceOfRoomRequestDTO);
        return ResponseEntity.ok(result);
    }
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/remove_device_from_room/{deviceOfRoomId}")
    public ResponseEntity<Boolean> removeDeviceFromRoom(@PathVariable String deviceOfRoomId) {
        boolean result = deviceOfRoomService.deleteDeviceOfRoom(deviceOfRoomId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get_all_devices_of_room/{roomId}")
    public ResponseEntity<?> getAllDevicesOfRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(deviceOfRoomService.getAllDevicesOfRoom(roomId));
    }
}
