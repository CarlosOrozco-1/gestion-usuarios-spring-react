import client from './client'
import type { UserResponse, UserRequest } from '../types'

export const usersApi = {
  findAll: () => client.get<UserResponse[]>('/system-users').then((r) => r.data),
  findById: (id: number) => client.get<UserResponse>(`/system-users/${id}`).then((r) => r.data),
  create: (data: UserRequest) => client.post<UserResponse>('/system-users', data).then((r) => r.data),
  update: (id: number, data: UserRequest) => client.put<UserResponse>(`/system-users/${id}`, data).then((r) => r.data),
  deactivate: (id: number) => client.delete(`/system-users/${id}`).then((r) => r.data),
  reactivate: (id: number) => client.patch(`/system-users/${id}/reactivate`).then((r) => r.data),
}
