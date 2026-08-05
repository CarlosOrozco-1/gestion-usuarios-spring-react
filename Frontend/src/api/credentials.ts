import client from './client'
import type { CredentialResponse, CredentialRequest } from '../types'

export const credentialsApi = {
  findAll: () => client.get<CredentialResponse[]>('/credentials').then((r) => r.data),
  findByClientId: (clientId: number) => client.get<CredentialResponse[]>(`/credentials/client/${clientId}`).then((r) => r.data),
  findById: (id: number) => client.get<CredentialResponse>(`/credentials/${id}`).then((r) => r.data),
  create: (data: CredentialRequest) => client.post<CredentialResponse>('/credentials', data).then((r) => r.data),
  update: (id: number, data: CredentialRequest) => client.put<CredentialResponse>(`/credentials/${id}`, data).then((r) => r.data),
  delete: (id: number) => client.delete(`/credentials/${id}`),
}
