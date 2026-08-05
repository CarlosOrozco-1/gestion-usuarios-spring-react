import client from './client'
import type { ClientResponse, ClientRequest } from '../types'

export const clientsApi = {
  findAll: () => client.get<ClientResponse[]>('/clients').then((r) => r.data),
  findById: (id: number) => client.get<ClientResponse>(`/clients/${id}`).then((r) => r.data),
  create: (data: ClientRequest) => client.post<ClientResponse>('/clients', data).then((r) => r.data),
  update: (id: number, data: ClientRequest) => client.put<ClientResponse>(`/clients/${id}`, data).then((r) => r.data),
  deactivate: (id: number) => client.delete(`/clients/${id}`).then((r) => r.data),
  reactivate: (id: number) => client.patch(`/clients/${id}/reactivate`).then((r) => r.data),
}
