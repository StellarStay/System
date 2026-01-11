package code.controller.roles;

import code.model.dto.users.req.RoleRequestDTO;
import code.model.entity.users.RoleEntity;
import code.services.users.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PutMapping("/update_role/{roleId}")
    public ResponseEntity<Boolean> updateRole(@PathVariable("roleId") String roleId, @RequestBody RoleRequestDTO roleRequestDTO) {
        if (roleRequestDTO == null || roleId == null || roleId.isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }
        boolean updated = roleService.updateRole(roleId, roleRequestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete_role/{roleId}")
    public ResponseEntity<Boolean> deleteRole(@PathVariable("roleId") String roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }
        boolean deleted = roleService.deleteRole(roleId);
        return ResponseEntity.ok(deleted);
    }

   @GetMapping("/get_role_by_id/{roleId}")
    public ResponseEntity<RoleEntity> getRoleById(@PathVariable("roleId") String roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        RoleEntity roleEntity = roleService.getRoleById(roleId);
        if (roleEntity != null) {
            return ResponseEntity.ok(roleEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get_all_roles")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
