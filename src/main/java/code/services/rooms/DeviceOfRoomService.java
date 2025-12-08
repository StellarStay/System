package code.services.rooms;

import code.model.dto.rooms.DeviceOfRoomRequestDTO;
import code.model.entity.rooms.DeviceOfRoomEntity;

import java.util.List;

public interface DeviceOfRoomService {
    boolean insertDeviceToRoom(DeviceOfRoomRequestDTO deviceOfRoomRequestDTO);
    boolean updateDeviceOfRoom(String deviceOfRoomId, DeviceOfRoomRequestDTO deviceOfRoomRequestDTO);
    boolean deleteDeviceOfRoom(String deviceOfRoomId);
    List<DeviceOfRoomEntity> getAllDevicesOfRoom(String roomId);

}
