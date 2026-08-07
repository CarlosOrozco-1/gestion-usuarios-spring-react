import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

const features = [
  { title: 'Gestión de usuarios y roles', description: 'Controla el acceso con roles y permisos.' },
  { title: 'Clientes y credenciales', description: 'Administra cuentas y datos por cliente.' },
  { title: 'Seguridad centralizada', description: 'Acceso protegido con autenticación.' },
]

function FeatureList() {
  return (
    <ul className="relative space-y-5 text-sm text-blue-50">
      {features.map((feature) => (
        <li key={feature.title} className="flex items-start gap-3">
          <span className="mt-0.5 flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-white/15">
            <svg className="h-4 w-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
            </svg>
          </span>
          <div>
            <p className="font-medium text-white">{feature.title}</p>
            <p className="text-blue-100">{feature.description}</p>
          </div>
        </li>
      ))}
    </ul>
  )
}

export function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login({ email, password })
      navigate('/', { replace: true })
    } catch (err: any) {
      setError(err.response?.data?.message || 'Credenciales inválidas')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen">
      <div className="relative hidden flex-1 flex-col justify-between overflow-hidden bg-gradient-to-br from-indigo-600 via-blue-700 to-slate-800 p-12 text-white lg:flex">
        <div className="pointer-events-none absolute -top-24 -right-24 h-96 w-96 rounded-full bg-white/10 blur-2xl" />
        <div className="pointer-events-none absolute -bottom-32 -left-16 h-80 w-80 rounded-full bg-white/5 blur-2xl" />

        <div className="relative">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/15">
              <svg className="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
              </svg>
            </div>
            <h1 className="text-2xl font-bold">Gestión de Usuarios</h1>
          </div>
        </div>

        <div className="relative">
          <h2 className="max-w-md text-4xl font-bold leading-tight">
            Administra usuarios, roles y credenciales en un solo lugar.
          </h2>
          <p className="mt-4 max-w-md text-blue-100">
            Plataforma central para el control de acceso y la administración de cuentas de tu organización.
          </p>
        </div>

        <FeatureList />
      </div>

      <div className="flex flex-1 items-center justify-center bg-blue-50 px-6 py-12">
        <div className="w-full max-w-sm">
          <div className="mb-8 text-center lg:hidden">
            <h1 className="text-2xl font-bold text-gray-800">Gestión de Usuarios</h1>
            <p className="mt-1 text-sm text-gray-500">Inicia sesión para continuar</p>
          </div>

          <form onSubmit={handleSubmit} className="rounded-2xl bg-white p-8 shadow-xl">
            <h2 className="text-xl font-bold text-gray-800">Iniciar sesión</h2>
            <p className="mb-6 mt-1 text-sm text-gray-500">Accede a tu cuenta con tus credenciales.</p>

            {error && (
              <div className="mb-4 rounded-lg border border-red-300 bg-red-50 px-4 py-2 text-sm text-red-600">
                {error}
              </div>
            )}

            <div className="mb-4">
              <label className="mb-1 block text-sm font-medium text-gray-700">Correo electrónico</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              />
            </div>

            <div className="mb-6">
              <label className="mb-1 block text-sm font-medium text-gray-700">Contraseña</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
            >
              {loading ? 'Ingresando…' : 'Iniciar sesión'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
