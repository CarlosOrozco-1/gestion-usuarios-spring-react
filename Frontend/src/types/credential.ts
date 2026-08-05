export interface CredentialResponse {
  id: number
  clientId: number
  clientName: string
  systemName: string
  username: string
  encryptedPassword: string
  url: string
  notes: string
  createdAt: string
  updatedAt: string
}

export interface CredentialRequest {
  clientId: number
  systemName: string
  username: string
  encryptedPassword: string
  url?: string
  notes?: string
}
