package com.example.authms.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequestDto {
    @NotBlank(message = "role name bosh ola bilmez")
    private String name;
}
