package com.gestionusuarios.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank
    private String idNumber;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String password;

    @NotNull
    private Integer roleId;
}
