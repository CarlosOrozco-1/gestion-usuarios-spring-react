export interface ClientResponse {
  id: number
  idNumber: string
  name: string
  email: string
  phone: string
  address: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface ClientRequest {
  idNumber: string
  name: string
  email: string
  phone?: string
  address?: string
}
