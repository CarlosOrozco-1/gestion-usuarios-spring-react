import client from './client'
import type { RoleResponse, RoleRequest } from '../types'

export const rolesApi = {
  findAll: () =>
    client.get<RoleResponse[]>('/roles').then((r) => r.data),

  findById: (id: number) =>
    client.get<RoleResponse>(`/roles/${id}`).then((r) => r.data),

  create: (data: RoleRequest) =>
    client.post<RoleResponse>('/roles', data).then((r) => r.data),

  update: (id: number, data: RoleRequest) =>
    client.put<RoleResponse>(`/roles/${id}`, data).then((r) => r.data),

  delete: (id: number) =>
    client.delete(`/roles/${id}`),

  assignPermissions: (roleId: number, permissionIds: number[]) =>
    client.post<RoleResponse>(`/roles/${roleId}/permissions`, permissionIds).then((r) => r.data),
}
