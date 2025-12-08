package code.services.rooms;

import code.model.dto.rooms.DeviceRequestDTO;
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
        DevicesEntity devicesEntity = devicesRepository.findById(deviceId).orElse(null);
        if(devicesEntity == null){
            return  false;
        }
        devicesEntity.setDeviceName(deviceRequestDTO.getDeviceName());
        devicesEntity.setDeviceType(deviceRequestDTO.getDeviceType());
        devicesEntity.setBrand(deviceRequestDTO.getBrand());
        devicesRepository.save(devicesEntity);
        return true;
    }

    @Override
    public boolean deleteDevice(String deviceId) {
        DevicesEntity devicesEntity = devicesRepository.findById(deviceId).orElse(null);
        if(devicesEntity == null){
            return  false;
        }
        devicesRepository.delete(devicesEntity);
        return true;
    }

    @Override
    public List<DevicesEntity> getAllDevices() {
        return devicesRepository.findAll();
    }

    @Override
    public DevicesEntity getDeviceById(String deviceId) {
        return devicesRepository.findById(deviceId).orElse(null);
    }
}
