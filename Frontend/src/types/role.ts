import type { PermissionResponse } from './permission'

export interface RoleResponse {
  id: number
  name: string
  description: string
  status: string
  createdAt: string
  permissions: PermissionResponse[]
}

export interface RoleRequest {
  name: string
  description?: string
}
