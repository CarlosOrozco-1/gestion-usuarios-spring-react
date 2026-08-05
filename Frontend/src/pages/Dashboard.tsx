import { useAuth } from '../hooks/useAuth'

export function Dashboard() {
  const { user } = useAuth()

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-800">Panel</h1>
      <div className="grid gap-6 md:grid-cols-3">
        <div className="rounded-lg bg-white p-6 shadow">
          <h2 className="text-sm font-medium text-gray-500">Rol</h2>
          <p className="mt-1 text-2xl font-bold text-gray-800">{user?.roleName}</p>
        </div>
        <div className="rounded-lg bg-white p-6 shadow">
          <h2 className="text-sm font-medium text-gray-500">Estado</h2>
          <p className="mt-1 text-2xl font-bold text-green-600">{user?.status}</p>
        </div>
        <div className="rounded-lg bg-white p-6 shadow">
          <h2 className="text-sm font-medium text-gray-500">Número de identificación</h2>
          <p className="mt-1 text-2xl font-bold text-gray-800">{user?.idNumber}</p>
        </div>
      </div>
    </div>
  )
}
