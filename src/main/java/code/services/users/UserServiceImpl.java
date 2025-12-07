package code.services.users;

import code.model.dto.users.UserRequestDTO;
import code.model.entity.users.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public boolean insertUser(UserRequestDTO userRequestDTO) {
        return false;
    }

    @Override
    public boolean updateUser(String userId, UserRequestDTO userRequestDTO) {
        return false;
    }

    @Override
    public boolean deleteUser(String userId) {
        return false;
    }

    @Override
    public UserEntity getUser(String userId) {
        return null;
    }

    @Override
    public List<UserEntity> getUsers() {
        return List.of();
    }
}
