import client from './client'
import type { UserResponse, UserRequest } from '../types'

export const usersApi = {
  findAll: () =>
    client.get<UserResponse[]>('/users').then((r) => r.data),

  findById: (id: number) =>
    client.get<UserResponse>(`/users/${id}`).then((r) => r.data),

  create: (data: UserRequest) =>
    client.post<UserResponse>('/users', data).then((r) => r.data),

  update: (id: number, data: UserRequest) =>
    client.put<UserResponse>(`/users/${id}`, data).then((r) => r.data),

  deactivate: (id: number) =>
    client.delete(`/users/${id}`),

  reactivate: (id: number) =>
    client.patch(`/users/${id}/reactivate`),
}
