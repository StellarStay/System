package code.services.rooms;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.model.dto.rooms.req.DeviceOfRoomRequestDTO;
import code.model.dto.rooms.res.DeviceOfRoomResponseDTO;
import code.model.dto.rooms.req.DeviceOfRoomUpdateDTO;
import code.model.entity.rooms.DeviceOfRoomEntity;
import code.model.entity.rooms.DevicesEntity;
import code.model.entity.rooms.RoomEntity;
import code.repository.rooms.DeviceOfRoomRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        if (deviceOfRoomRequestDTO == null){
            throw new BadRequestException("DeviceOfRoomRequestDTO is null");
        }
        RoomEntity roomEntity = roomsService.getRoomById(deviceOfRoomRequestDTO.getRoomId());
        if (roomEntity == null) {
            throw new ResourceNotFoundException("Room Not Found");
        }

        // Lặp qua tất cả deviceIds và insert từng device vào phòng
        for (String deviceId : deviceOfRoomRequestDTO.getDeviceIds()) {
            DevicesEntity devicesEntity = deviceService.getDeviceById(deviceId);
            if (devicesEntity == null) {
                continue; // Bỏ qua device không tồn tại
            }

            DeviceOfRoomEntity deviceOfRoomEntity = new DeviceOfRoomEntity();
            deviceOfRoomEntity.setDeviceOfRoomId(generateDeviceOfRoomId());
            deviceOfRoomEntity.setRoom(roomEntity);
            deviceOfRoomEntity.setDevice(devicesEntity);
            deviceOfRoomEntity.setStatus(deviceOfRoomRequestDTO.isStatus());
            deviceOfRoomEntity.setCreatedDate(LocalDateTime.now());
            deviceOfRoomRepository.save(deviceOfRoomEntity);
        }
        return true;
    }

    @Override
    public boolean updateDeviceOfRoom(String deviceOfRoomId, DeviceOfRoomUpdateDTO deviceOfRoomUpdateDTO) {
        if (deviceOfRoomId == null || deviceOfRoomUpdateDTO == null){
            throw new BadRequestException("DeviceOfRoomId or DeviceOfRoomUpdateDTO is null");
        }
        DeviceOfRoomEntity deviceOfRoomEntity = deviceOfRoomRepository.findById(deviceOfRoomId).orElse(null);
        if(deviceOfRoomEntity == null) {
            throw new ResourceNotFoundException("DeviceOfRoom Not Found");
        }
        RoomEntity roomEntity = roomsService.getRoomById(deviceOfRoomUpdateDTO.getRoomId());
        DevicesEntity devicesEntity = deviceService.getDeviceById(deviceOfRoomUpdateDTO.getDeviceId());
        deviceOfRoomEntity.setRoom(roomEntity);
        deviceOfRoomEntity.setDevice(devicesEntity);
        deviceOfRoomEntity.setStatus(false);
        deviceOfRoomRepository.save(deviceOfRoomEntity);
        return true;
    }

    @Override
    public boolean deleteDeviceOfRoom(String deviceOfRoomId) {
        if (deviceOfRoomId == null) {
            throw new BadRequestException("DeviceOfRoomId is null");
        }
        DeviceOfRoomEntity deviceOfRoomEntity = deviceOfRoomRepository.findById(deviceOfRoomId).orElse(null);
        if(deviceOfRoomEntity == null) {
            throw new ResourceNotFoundException("DeviceOfRoom Not Found");
        }
        deviceOfRoomRepository.delete(deviceOfRoomEntity);
        return true;
    }

    @Override
    public List<DeviceOfRoomResponseDTO> getAllDevicesOfRoom(String roomId) {
        if (roomId == null) {
            throw new BadRequestException("RoomId is null");
        }
        List<DeviceOfRoomEntity> deviceOfRoomEntities = deviceOfRoomRepository.findByRoom_RoomId(roomId);
        List<DeviceOfRoomResponseDTO> deviceOfRoomResponseDTOS = new ArrayList<>();
        for (DeviceOfRoomEntity deviceOfRoomEntity : deviceOfRoomEntities) {
            DeviceOfRoomResponseDTO dto = new DeviceOfRoomResponseDTO();

            dto.setDeviceName(deviceOfRoomEntity.getDevice().getDeviceName());
            dto.setStatus(deviceOfRoomEntity.isStatus());

            deviceOfRoomResponseDTOS.add(dto);
        }
        return deviceOfRoomResponseDTOS;
    }
}
