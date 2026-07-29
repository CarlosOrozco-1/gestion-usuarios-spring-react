export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  expiresIn: number
  user: UserSummary
}

export interface UserSummary {
  id: number
  idNumber: string
  name: string
  email: string
  roleName: string
  status: string
  active: boolean
  createdAt: string
  updatedAt: string
}
