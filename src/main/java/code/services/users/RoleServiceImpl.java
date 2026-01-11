package code.services.users;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.model.dto.users.req.RoleRequestDTO;
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
        String randomId;
        do{
            randomId = RandomId.generateRoomId(max_length_id);
        }while(roleRepository.findById(randomId).isPresent());
        return randomId;
    }

    @Override
    public boolean updateRole(String roleId, RoleRequestDTO roleRequestDTO) {
        if (roleId == null || roleRequestDTO == null) {
            throw new BadRequestException("RoleId and RoleRequestDTO is null");
        }
        RoleEntity roleEntity = roleRepository.findById(roleId).orElse(null);
        if (roleEntity == null) {
            throw new ResourceNotFoundException("RoleEntity is null");
        } else {
            roleEntity.setRoleName(roleRequestDTO.getRoleName());
            roleEntity.setDescription(roleRequestDTO.getDescription());
            roleRepository.save(roleEntity);
            return true;
        }
    }

    @Override
    public boolean deleteRole(String roleId) {
        if (roleId == null) {
            throw new BadRequestException("RoleId and RoleRequestDTO is null");
        }
        RoleEntity roleEntity = roleRepository.findById(roleId).orElse(null);
        if (roleEntity == null) {
            throw new ResourceNotFoundException("RoleEntity is null");
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
        if (roleId == null) {
            throw new BadRequestException("RoleId is null");
        }
        return roleRepository.findById(roleId).orElse(null);
    }
}
