export interface UserResponse {
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

export interface UserRequest {
  idNumber: string
  name: string
  email: string
  password?: string
  roleId: number
}
