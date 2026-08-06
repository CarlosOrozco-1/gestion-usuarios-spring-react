package com.gestionusuarios.dto.stats;

import java.util.List;

public record DashboardStatsResponse(
        long totalUsers,
        long activeUsers,
        long inactiveUsers,
        long totalClients,
        long activeClients,
        long inactiveClients,
        long totalCredentials,
        long totalRoles,
        long totalPermissions,
        List<RoleUserCount> usersByRole,
        List<ClientCredentialCount> credentialsByClient,
        List<RecentUser> recentUsers
) {
}
