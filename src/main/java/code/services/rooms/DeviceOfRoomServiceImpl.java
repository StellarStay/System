package code.services.rooms;

import code.model.dto.rooms.DeviceOfRoomRequestDTO;
import code.model.entity.rooms.DeviceOfRoomEntity;
import code.model.entity.rooms.DevicesEntity;
import code.model.entity.rooms.RoomEntity;
import code.repository.rooms.DeviceOfRoomRepository;
import code.repository.rooms.RoomRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceOfRoomServiceImpl implements DeviceOfRoomService {
    @Autowired
    private DeviceOfRoomRepository deviceOfRoomRepository;
    @Autowired
    private RoomsService roomsService;
    @Autowired
    private DeviceService deviceService;

    int max_length_device_of_room_id = 10;
    private String generateDeviceOfRoomId() {
        String randomDeviceOfRoomId;
        do{
            randomDeviceOfRoomId = RandomId.generateRoomId(max_length_device_of_room_id);
        }while(deviceOfRoomRepository.existsById(randomDeviceOfRoomId));
        return randomDeviceOfRoomId;
    }
    @Override
    public boolean insertDeviceToRoom(DeviceOfRoomRequestDTO deviceOfRoomRequestDTO) {
        DeviceOfRoomEntity deviceOfRoomEntity = new DeviceOfRoomEntity();
        RoomEntity roomEntity = roomsService.getRoomById(deviceOfRoomRequestDTO.getRoomId());
        DevicesEntity devicesEntity = deviceService.getDeviceById(deviceOfRoomRequestDTO.getDeviceId());
        deviceOfRoomEntity.setDeviceOfRoomId(generateDeviceOfRoomId());
        deviceOfRoomEntity.setRoom(roomEntity);
        deviceOfRoomEntity.setDevice(devicesEntity);
        deviceOfRoomEntity.setStatus(deviceOfRoomRequestDTO.isStatus());
        deviceOfRoomEntity.setCreatedDate(LocalDateTime.now());
        deviceOfRoomRepository.save(deviceOfRoomEntity);
        return true;
    }

    @Override
    public boolean updateDeviceOfRoom(String deviceOfRoomId, DeviceOfRoomRequestDTO deviceOfRoomRequestDTO) {
        DeviceOfRoomEntity deviceOfRoomEntity = deviceOfRoomRepository.findById(deviceOfRoomId).orElse(null);
        if(deviceOfRoomEntity == null) {
            return false;
        }
        RoomEntity roomEntity = roomsService.getRoomById(deviceOfRoomRequestDTO.getRoomId());
        DevicesEntity devicesEntity = deviceService.getDeviceById(deviceOfRoomRequestDTO.getDeviceId());
        deviceOfRoomEntity.setRoom(roomEntity);
        deviceOfRoomEntity.setDevice(devicesEntity);
        deviceOfRoomEntity.setStatus(deviceOfRoomRequestDTO.isStatus());
        deviceOfRoomRepository.save(deviceOfRoomEntity);
        return true;
    }

    @Override
    public boolean deleteDeviceOfRoom(String deviceOfRoomId) {
        DeviceOfRoomEntity deviceOfRoomEntity = deviceOfRoomRepository.findById(deviceOfRoomId).orElse(null);
        if(deviceOfRoomEntity == null) {
            return false;
        }
        deviceOfRoomRepository.delete(deviceOfRoomEntity);
        return true;
    }

    @Override
    public List<DeviceOfRoomEntity> getAllDevicesOfRoom(String roomId) {
        return deviceOfRoomRepository.findAll();
    }
}
