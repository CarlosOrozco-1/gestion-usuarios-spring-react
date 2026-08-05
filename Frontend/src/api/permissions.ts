import client from './client'
import type { PermissionResponse, PermissionRequest } from '../types'

export const permissionsApi = {
  findAll: () => client.get<PermissionResponse[]>('/permissions').then((r) => r.data),
  findById: (id: number) => client.get<PermissionResponse>(`/permissions/${id}`).then((r) => r.data),
  create: (data: PermissionRequest) => client.post<PermissionResponse>('/permissions', data).then((r) => r.data),
  update: (id: number, data: PermissionRequest) => client.put<PermissionResponse>(`/permissions/${id}`, data).then((r) => r.data),
  delete: (id: number) => client.delete(`/permissions/${id}`),
}
