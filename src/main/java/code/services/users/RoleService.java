package code.services.users;

import code.model.dto.users.RoleRequestDTO;
import code.model.entity.users.RoleEntity;

import java.util.List;

public interface RoleService {
    boolean insertRole(RoleRequestDTO roleRequestDTO);
    boolean updateRole(String roleId, RoleRequestDTO roleRequestDTO);
    boolean deleteRole(String roleId);
    List<RoleEntity> getAllRoles();
    RoleEntity getRoleById(String roleId);
}
