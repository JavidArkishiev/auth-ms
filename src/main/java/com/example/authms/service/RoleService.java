package com.example.authms.service;

import com.example.authms.dto.request.RoleRequestDto;
import com.example.authms.entity.Role;
import com.example.authms.entity.User;
import com.example.authms.exception.AllException;
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


    public void createRole(RoleRequestDto role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new AllException("bu rol sistemde movcuddur");
        }
        Role roleEntity = roleMapper.mapToRoleEntity(role);
        roleRepository.save(roleEntity);
    }

    public Role findById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new AllException("role tapılmadı"));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();

    }

    public void assignUserToRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AllException("istifadəçi tapılmadı"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AllException("role tapılmadı"));

        if (user.getRoles().contains(role)) {
            throw new AllException(user.getName() + " artıq " + role.getName() + " rolu almışdır");
        }
        role.getUsers().add(user);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public List<Role> getAllRolesUser(String email) {
        return userRepository.findAllByEmail(email);
    }


    public void removeUserFromRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AllException("istifadəçi tapılmadı"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AllException("role tapılmadı"));
        if (!user.getRoles().contains(role)) {
            throw new AllException("bu rol istifadəçiden artıq silinib");
        }
        user.getRoles().remove(role);
        role.getUsers().remove(user);
        roleRepository.save(role);

    }

    public void removeAllUserFromRole(Long roleId) {
        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AllException("role tapilmadi"));
        if (role.getUsers().isEmpty()) {
            throw new AllException("Bu rol artıq bütün istifadəçilərdən silinib");

        }
        role.getUsers().forEach(user -> {
            user.getRoles().remove(role);
        });

        roleRepository.save(role);
    }


    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AllException("role tapılmadı"));
        roleRepository.delete(role);

    }
}
