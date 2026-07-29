import { createContext, useState, useCallback, type ReactNode } from 'react'
import { authApi } from '../api/auth'
import type { LoginRequest, UserSummary } from '../types'

interface AuthState {
  user: UserSummary | null
  token: string | null
  isAuthenticated: boolean
  login: (data: LoginRequest) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthState>({} as AuthState)

function loadUser(): UserSummary | null {
  const raw = localStorage.getItem('user')
  return raw ? JSON.parse(raw) : null
}

function loadToken(): string | null {
  return localStorage.getItem('token')
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserSummary | null>(loadUser)
  const [token, setToken] = useState<string | null>(loadToken)

  const login = useCallback(async (data: LoginRequest) => {
    const res = await authApi.login(data)
    localStorage.setItem('token', res.token)
    localStorage.setItem('user', JSON.stringify(res.user))
    setToken(res.token)
    setUser(res.user)
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setToken(null)
    setUser(null)
  }, [])

  const isAuthenticated = !!token && !!user

  return (
    <AuthContext.Provider value={{ user, token, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}
