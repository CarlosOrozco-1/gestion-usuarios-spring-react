package com.gestionusuarios.service;

import com.gestionusuarios.dto.stats.DashboardStatsResponse;
import com.gestionusuarios.dto.stats.RecentUser;
import com.gestionusuarios.repository.ClientRepository;
import com.gestionusuarios.repository.CredentialRepository;
import com.gestionusuarios.repository.PermissionRepository;
import com.gestionusuarios.repository.RoleRepository;
import com.gestionusuarios.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SystemUserRepository systemUserRepository;
    private final ClientRepository clientRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        long totalUsers = systemUserRepository.count();
        long activeUsers = systemUserRepository.countByActive(true);
        long totalClients = clientRepository.count();
        long activeClients = clientRepository.countByActive(true);

        return new DashboardStatsResponse(
                totalUsers,
                activeUsers,
                totalUsers - activeUsers,
                totalClients,
                activeClients,
                totalClients - activeClients,
                credentialRepository.count(),
                roleRepository.count(),
                permissionRepository.count(),
                systemUserRepository.countUsersByRole(),
                credentialRepository.countCredentialsByClient(),
                systemUserRepository.findTop5ByOrderByCreatedAtDesc().stream()
                        .map(u -> new RecentUser(u.getId(), u.getName(), u.getEmail(), u.getRole().getName(), u.getCreatedAt()))
                        .toList()
        );
    }
}
