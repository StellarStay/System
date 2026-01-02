package code.services.rooms;

import code.model.dto.rooms.DeviceRequestDTO;
import code.model.dto.rooms.DeviceResponseDTO;
import code.model.entity.rooms.DevicesEntity;

import java.util.List;

public interface DeviceService {
    boolean insertDevice(DeviceRequestDTO deviceRequestDTO);
    boolean updateDevice(String deviceId, DeviceRequestDTO deviceRequestDTO);
    boolean deleteDevice(String deviceId);
    List<DeviceResponseDTO> getAllDevices();
    DevicesEntity getDeviceById(String deviceId);
    DeviceResponseDTO getDeviceByDeviceId(String deviceId);

}
