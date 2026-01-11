package code.controller.devices;

import code.exception.BadRequestException;
import code.model.dto.rooms.req.DeviceRequestDTO;
import code.model.dto.rooms.res.DeviceResponseDTO;
import code.services.rooms.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/insert_device")
    public ResponseEntity<Boolean> insertDevice(@RequestBody DeviceRequestDTO deviceRequestDTO) {
        if (deviceRequestDTO == null) {
            throw new BadRequestException("DeviceRequestDTO is null");
        }
        return ResponseEntity.ok(deviceService.insertDevice(deviceRequestDTO));
    }

    @PutMapping("/update_device/{deviceId}")
    public ResponseEntity<Boolean> updateDevice(@PathVariable String deviceId, @RequestBody DeviceRequestDTO deviceRequestDTO) {
        if (deviceRequestDTO == null || deviceId == null || deviceId.isEmpty()) {
            throw new BadRequestException("Some fields is null");
        }
        return ResponseEntity.ok(deviceService.updateDevice(deviceId, deviceRequestDTO));
    }

    @DeleteMapping("/delete_device/{deviceId}")
    public ResponseEntity<Boolean> deleteDevice(@PathVariable String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            throw new BadRequestException("DeviceId is null");
        }
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/get_all_devices")
    public ResponseEntity<?> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/get_device_by_id/{deviceId}")
    public ResponseEntity<DeviceResponseDTO> getDeviceById(@PathVariable String deviceId){
        if (deviceId == null || deviceId.isEmpty()) {
            throw new BadRequestException("DeviceId is null");
        }
        DeviceResponseDTO deviceResponseDTO = deviceService.getDeviceByDeviceId(deviceId);
        return ResponseEntity.ok(deviceResponseDTO);
    }
}
