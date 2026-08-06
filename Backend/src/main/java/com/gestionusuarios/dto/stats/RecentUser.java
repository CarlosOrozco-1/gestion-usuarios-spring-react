package com.gestionusuarios.dto.stats;

import java.time.LocalDateTime;

public record RecentUser(Integer id, String name, String email, String roleName, LocalDateTime createdAt) {
}
