package com.gestionusuarios.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    @NotBlank
    @Size(max = 50)
    private String idNumber;

    @NotBlank
    @Size(max = 50)
    private String nit;

    @NotBlank
    @Size(max = 200)
    private String businessName;

    @NotBlank
    @Size(max = 100)
    private String taxRegime;

    private LocalDate birthDate;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String address;
}