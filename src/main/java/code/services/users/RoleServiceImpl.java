package code.services.users;

import code.model.dto.users.RoleRequestDTO;
import code.model.entity.users.RoleEntity;
import code.repository.users.RoleRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    int max_length_id = 8;

    private String generateRoleId() {
        return RandomId.generateRoomId(max_length_id);
    }

    @Override
    public boolean insertRole(RoleRequestDTO roleRequestDTO) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(generateRoleId());
        roleEntity.setRoleName(roleRequestDTO.getRoleName());
        roleEntity.setDescription(roleRequestDTO.getDescription());
        return true;
    }

    @Override
    public boolean updateRole(String roleId, RoleRequestDTO roleRequestDTO) {
        RoleEntity roleEntity = roleRepository.findById(roleId).orElse(null);
        if (roleEntity == null) {
            return false;
        } else {
            roleEntity.setRoleName(roleRequestDTO.getRoleName());
            roleEntity.setDescription(roleRequestDTO.getDescription());
            return true;
        }
    }

    @Override
    public boolean deleteRole(String roleId) {
        RoleEntity roleEntity = roleRepository.findById(roleId).orElse(null);
        if (roleEntity == null) {
            return false;
        } else {
            roleRepository.delete(roleEntity);
            return true;
        }
    }

    @Override
    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public RoleEntity getRoleById(String roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }
}
