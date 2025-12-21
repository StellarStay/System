package code.services.users;

import code.model.dto.users.UserRequestDTO;
import code.model.entity.users.RoleEntity;
import code.model.entity.users.UserEntity;
import code.repository.users.UserRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    int max_length_id = 8;

    private String generateUserId() {
        String randomId;
        do{
            randomId = RandomId.generateRoomId(max_length_id);
        }while(userRepository.findById(randomId).isPresent());
        return randomId;
    }

    @Override
    public boolean insertUser(UserRequestDTO userRequestDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(generateUserId());
        userEntity.setIdCard(userRequestDTO.getIdCard());
        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setDateOfBirth(userRequestDTO.getDateOfBirth());
        userEntity.setPhone(userRequestDTO.getPhone());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setPassword(userRequestDTO.getPassword());
        userEntity.setGender(userRequestDTO.isGender());
        userEntity.setCreatedAt(LocalDateTime.now());

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(userRequestDTO.getRoleId());

        userEntity.setRole(roleEntity);
        userEntity.setStatus(true);
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean updateUser(String userId, UserRequestDTO userRequestDTO) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            return false;
        }
        userEntity.setIdCard(userRequestDTO.getIdCard());
        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setDateOfBirth(userRequestDTO.getDateOfBirth());
        userEntity.setPhone(userRequestDTO.getPhone());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setPassword(userRequestDTO.getPassword());
        userEntity.setGender(userRequestDTO.isGender());

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(userRequestDTO.getRoleId());

        userEntity.setRole(roleEntity);
        userRepository.save(userEntity);
        return true;

    }

    @Override
    public boolean deleteUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            return false;
        } else {
            userRepository.delete(userEntity);
            return true;
        }
    }

    @Override
    public UserEntity getUser(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean isEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
