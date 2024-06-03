package com.example.authms.controller;

import com.example.authms.dto.RoleRequestDto;
import com.example.authms.entity.Role;
import com.example.authms.entity.User;
import com.example.authms.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("roles")
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {
    private final RoleService roleService;


    @PostMapping()
    public ResponseEntity<RoleRequestDto> crateRole(@RequestBody RoleRequestDto role) {
        return new ResponseEntity<>(roleService.createRole(role), CREATED);
    }

    @PostMapping("assign-role-to-user")
    public User assignUserToRole(@RequestParam Long userId,
                                 @RequestParam Long roleId) {
        return roleService.assignUserToRole(userId, roleId);
    }


    @GetMapping()
    public ResponseEntity<List<Role>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), OK);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        return new ResponseEntity<>(roleService.findById(roleId), OK);
    }

    @GetMapping("all-roles-user")
    public List<Role> getAllRolesUser(@RequestParam Long userId) {
        return roleService.getAllRolesUser(userId);

    }


    @DeleteMapping("/{roleId}")
    @ResponseStatus(OK)
    public String deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return "SUCCESS";
    }


    @DeleteMapping("remove-all-role-from-users/{roleId}")
    public Role removeAllUsersFromRole(@PathVariable Long roleId) {
        return roleService.removeAllUserFromRole(roleId);
    }


    @DeleteMapping("remove-role-from-user")
    public User removeUserFromRole(@RequestParam Long userId,
                                   @RequestParam Long roleId) {
        return roleService.removeUserFromRole(userId, roleId);
    }


}
