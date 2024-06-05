package com.example.authms.service;

import com.example.authms.dto.request.RoleRequestDto;
import com.example.authms.entity.Role;
import com.example.authms.entity.User;
import com.example.authms.exception.UserNotFoundException;
import com.example.authms.mapper.RoleMapper;
import com.example.authms.repository.RoleRepository;
import com.example.authms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();

    }

    public RoleRequestDto createRole(RoleRequestDto role) {
        Optional<Role> checkRole = roleRepository.findByName(role.getName());
        if (checkRole.isPresent()) {
            throw new UserNotFoundException(checkRole.get().getName() + " role already exist");
        }
        Role roleEntity = roleMapper.mapToRoleEntity(role);
        roleRepository.save(roleEntity);
        return role;
    }

    public Role findById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new UserNotFoundException("role not found"));
    }

    public void deleteRole(Long roleId) {
        if (findById(roleId) != null) {
            removeAllUserFromRole(roleId);
            roleRepository.deleteById(roleId);
        }

    }
//    public Role findByName(String name) {
//        return roleRepository.findByName(name).get();
//
//    }

    public Role removeAllUserFromRole(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.ifPresent(Role::removeAllUsersFromRole);
        return roleRepository.save(role.get());
    }

    public User assignUserToRole(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (user.isPresent() && user.get().getRoles().contains(role.get())) {
            throw new UserNotFoundException(
                    user.get().getName() + " is already assigned to the " + role.get().getName() + " role");
        }
        role.ifPresent(Role -> Role.assignUserToRole(user.get()));
        roleRepository.save(role.get());
        return user.get();

    }

    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent() && role.get().getUsers().contains(user.get())) {
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UserNotFoundException("User not found!");
    }

    public List<Role> getAllRolesUser(Long userId) {
        return userRepository.findAllByUserId(userId);

    }
}
