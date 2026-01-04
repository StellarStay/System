package code.services.rooms;

import code.model.dto.rooms.DeviceOfRoomRequestDTO;
import code.model.dto.rooms.DeviceOfRoomResponseDTO;
import code.model.dto.rooms.DeviceOfRoomUpdateDTO;


import java.util.List;

public interface DeviceOfRoomService {
    boolean insertDeviceToRoom(DeviceOfRoomRequestDTO deviceOfRoomRequestDTO);
    boolean updateDeviceOfRoom(String deviceOfRoomId, DeviceOfRoomUpdateDTO deviceOfRoomUpdateDTO);
    boolean deleteDeviceOfRoom(String deviceOfRoomId);
    List<DeviceOfRoomResponseDTO> getAllDevicesOfRoom(String roomId);
}
