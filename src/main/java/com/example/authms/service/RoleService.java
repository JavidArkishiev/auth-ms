package com.example.authms.service;

import com.example.authms.dto.request.RoleRequestDto;
import com.example.authms.entity.Role;
import com.example.authms.entity.User;
import com.example.authms.exception.ExistEmailException;
import com.example.authms.exception.UserNotFoundException;
import com.example.authms.mapper.RoleMapper;
import com.example.authms.repository.RoleRepository;
import com.example.authms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;


    public void createRole(RoleRequestDto role) throws ExistEmailException {
        if (roleRepository.existsByName(role.getName())) {
            throw new ExistEmailException("Bu rol sistemde movcuddur");
        }
        Role roleEntity = roleMapper.mapToRoleEntity(role);
        roleRepository.save(roleEntity);
    }

    public Role findById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new UserNotFoundException("Role tapılmadı"));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();

    }

    public void assignUserToRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserNotFoundException("Role tapılmadı"));

        if (user.getRoles().contains(role)) {
            throw new UserNotFoundException(user.getFullName() + " artıq " + role.getName() + " rolu almışdır");
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
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserNotFoundException("Role tapılmadı"));
        if (!user.getRoles().contains(role)) {
            throw new UserNotFoundException("Bu rol istifadəçiden artıq silinib");
        }
        user.getRoles().remove(role);
        role.getUsers().remove(user);
        roleRepository.save(role);

    }

    public void removeAllUserFromRole(Long roleId) {
        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserNotFoundException("Role tapilmadi"));
        if (role.getUsers().isEmpty()) {
            throw new UserNotFoundException("Bu rol artıq bütün istifadəçilərdən silinib");

        }
        role.getUsers().forEach(user -> user.getRoles().remove(role));

        roleRepository.save(role);
    }


    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserNotFoundException("Role tapılmadı"));
        roleRepository.delete(role);

    }
}
