export interface ClientResponse {
  id: number
  idNumber: string
  nit: string
  nombreRazonSocial: string
  regimenFiscal: string
  fechaNacimiento: string | null
  email: string
  phone: string
  address: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface ClientRequest {
  idNumber: string
  nit: string
  nombreRazonSocial: string
  regimenFiscal: string
  fechaNacimiento?: string | null
  email: string
  phone?: string
  address?: string
}
