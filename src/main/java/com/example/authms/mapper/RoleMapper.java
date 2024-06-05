package com.example.authms.mapper;

import com.example.authms.dto.request.RoleRequestDto;
import com.example.authms.entity.Role;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role mapToRoleEntity(RoleRequestDto role);
}
