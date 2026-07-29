export interface PermissionResponse {
  id: number
  name: string
  description: string
  resourcePath: string
  createdAt: string
}

export interface PermissionRequest {
  name: string
  description?: string
  resourcePath?: string
}
