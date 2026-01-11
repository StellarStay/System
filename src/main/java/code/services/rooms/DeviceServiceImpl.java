package code.services.rooms;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.model.dto.rooms.req.DeviceRequestDTO;
import code.model.dto.rooms.res.DeviceResponseDTO;
import code.model.entity.rooms.DevicesEntity;
import code.repository.rooms.DevicesRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceServiceImpl implements  DeviceService {
    @Autowired
    private DevicesRepository devicesRepository;

    int max_length_device_id = 8;
    private String generateDeviceId(){
        String randomId;
        do{
            randomId = RandomId.generateRoomId(max_length_device_id);
        }while (devicesRepository.existsById(randomId));
        return randomId;
    }

    @Override
    public boolean insertDevice(DeviceRequestDTO deviceRequestDTO) {
        if (deviceRequestDTO == null){
            throw new BadRequestException("DeviceRequestDTO is null");
        }
        DevicesEntity devicesEntity = new DevicesEntity();
        devicesEntity.setDeviceId(generateDeviceId());
        devicesEntity.setDeviceName(deviceRequestDTO.getDeviceName());
        devicesEntity.setDeviceType(deviceRequestDTO.getDeviceType());
        devicesEntity.setBrand(deviceRequestDTO.getBrand());
        devicesRepository.save(devicesEntity);
        return true;
    }

    @Override
    public boolean updateDevice(String deviceId, DeviceRequestDTO deviceRequestDTO) {
        if (deviceId == null || deviceRequestDTO == null){
            throw new BadRequestException("DeviceId or DeviceRequestDTO is null");
        }
        DevicesEntity devicesEntity = devicesRepository.findById(deviceId).orElse(null);
        if(devicesEntity == null){
            throw new ResourceNotFoundException("Device not found");
        }
        devicesEntity.setDeviceName(deviceRequestDTO.getDeviceName());
        devicesEntity.setDeviceType(deviceRequestDTO.getDeviceType());
        devicesEntity.setBrand(deviceRequestDTO.getBrand());
        devicesRepository.save(devicesEntity);
        return true;
    }

    @Override
    public boolean deleteDevice(String deviceId) {
        if (deviceId == null) {
            throw new BadRequestException("DeviceId is null");
        }
        DevicesEntity devicesEntity = devicesRepository.findById(deviceId).orElse(null);
        if(devicesEntity == null){
            throw new ResourceNotFoundException("Device not found");
        }
        devicesRepository.delete(devicesEntity);
        return true;
    }

    @Override
    public List<DeviceResponseDTO> getAllDevices() {
        List<DevicesEntity> devicesEntities = devicesRepository.findAll();
        if (devicesEntities == null){
            throw new ResourceNotFoundException("Device List not found");
        }
        return devicesEntities.stream().map(device -> new DeviceResponseDTO(
                device.getDeviceId(),
                device.getDeviceName(),
                device.getBrand()
        )).toList();
    }

    @Override
    public DevicesEntity getDeviceById(String deviceId) {
        if (deviceId == null) {
            throw new BadRequestException("DeviceId is null");
        }
        return devicesRepository.findById(deviceId).orElse(null);
    }

    @Override
    public DeviceResponseDTO getDeviceByDeviceId(String deviceId) {
        if (deviceId == null) {
            throw new BadRequestException("DeviceId is null");
        }
        DevicesEntity devicesEntity = devicesRepository.findById(deviceId).orElse(null);
        if(devicesEntity == null){
            return null;
        }
        return new DeviceResponseDTO(
                devicesEntity.getDeviceId(),
                devicesEntity.getDeviceName(),
                devicesEntity.getBrand()
        );
    }
}
