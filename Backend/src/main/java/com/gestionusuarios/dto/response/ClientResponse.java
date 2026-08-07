package com.gestionusuarios.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Integer id;
    private String idNumber;
    private String nit;
    private String nombreRazonSocial;
    private String regimenFiscal;
    private LocalDate fechaNacimiento;
    private String email;
    private String phone;
    private String address;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}