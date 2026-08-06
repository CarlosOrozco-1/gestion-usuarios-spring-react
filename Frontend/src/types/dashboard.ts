export interface RoleUserCount {
  roleName: string
  count: number
}

export interface ClientCredentialCount {
  clientName: string
  count: number
}

export interface RecentUser {
  id: number
  name: string
  email: string
  roleName: string
  createdAt: string
}

export interface DashboardStats {
  totalUsers: number
  activeUsers: number
  inactiveUsers: number
  totalClients: number
  activeClients: number
  inactiveClients: number
  totalCredentials: number
  totalRoles: number
  totalPermissions: number
  usersByRole: RoleUserCount[]
  credentialsByClient: ClientCredentialCount[]
  recentUsers: RecentUser[]
}
