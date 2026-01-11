package code.services.rooms;

import code.model.dto.rooms.req.DeviceOfRoomRequestDTO;
import code.model.dto.rooms.res.DeviceOfRoomResponseDTO;
import code.model.dto.rooms.req.DeviceOfRoomUpdateDTO;


import java.util.List;

public interface DeviceOfRoomService {
    boolean insertDeviceToRoom(DeviceOfRoomRequestDTO deviceOfRoomRequestDTO);
    boolean updateDeviceOfRoom(String deviceOfRoomId, DeviceOfRoomUpdateDTO deviceOfRoomUpdateDTO);
    boolean deleteDeviceOfRoom(String deviceOfRoomId);
    List<DeviceOfRoomResponseDTO> getAllDevicesOfRoom(String roomId);
}
