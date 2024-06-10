package com.example.authms.controller;

import com.example.authms.dto.request.RoleRequestDto;
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
    public ResponseEntity<String> crateRole(@RequestBody RoleRequestDto role) {
        roleService.createRole(role);
        return ResponseEntity.ok("Yeni rol yaradıldı");
    }

    @PostMapping("assign-role-to-user")
    public String assignUserToRole(@RequestParam Long userId,
                                   @RequestParam Long roleId) {
        roleService.assignUserToRole(userId, roleId);
        return "İstifadəçiyə yeni rol verildi";
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
    public List<Role> getAllRolesUser(@RequestParam String email) {
        return roleService.getAllRolesUser(email);

    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(OK)
    public String deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return "SUCCESS";
    }

    @DeleteMapping("remove-all-role-from-users/{roleId}")
    public String removeAllUsersFromRole(@PathVariable Long roleId) {
        roleService.removeAllUserFromRole(roleId);
        return "Bu rol bütün istifadəçilərdən silindi";
    }

    @DeleteMapping("remove-role-from-user")
    public String removeUserFromRole(@RequestParam Long userId,
                                     @RequestParam Long roleId) {
        roleService.removeUserFromRole(userId, roleId);
        return "İstifadəçidən rol silindi";
    }
}
