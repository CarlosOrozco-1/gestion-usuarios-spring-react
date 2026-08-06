import { useEffect, useState } from 'react'
import { useAuth } from '../hooks/useAuth'
import { useToast } from '../hooks/useToast'
import { dashboardApi } from '../api/dashboard'
import type { DashboardStats } from '../types'

const BAR_COLORS = ['bg-blue-500', 'bg-emerald-500', 'bg-amber-500', 'bg-violet-500', 'bg-rose-500', 'bg-cyan-500']

function BarList({
  items,
  emptyText,
}: {
  items: { label: string; value: number }[]
  emptyText: string
}) {
  const max = Math.max(1, ...items.map((i) => i.value))
  if (items.length === 0) {
    return <p className="py-8 text-center text-sm text-gray-400">{emptyText}</p>
  }
  return (
    <div className="space-y-3">
      {items.map((item, idx) => (
        <div key={item.label}>
          <div className="mb-1 flex items-center justify-between text-sm">
            <span className="font-medium text-gray-700">{item.label}</span>
            <span className="text-gray-500">{item.value}</span>
          </div>
          <div className="h-2.5 w-full overflow-hidden rounded-full bg-gray-100">
            <div
              className={`h-full rounded-full ${BAR_COLORS[idx % BAR_COLORS.length]}`}
              style={{ width: `${(item.value / max) * 100}%` }}
            />
          </div>
        </div>
      ))}
    </div>
  )
}

function KpiCard({ label, value, accent }: { label: string; value: number; accent?: string }) {
  return (
    <div className="rounded-lg bg-white p-5 shadow">
      <p className="text-sm font-medium text-gray-500">{label}</p>
      <p className={`mt-1 text-3xl font-bold ${accent ?? 'text-gray-800'}`}>{value}</p>
    </div>
  )
}

export function Dashboard() {
  const toast = useToast()
  const { user } = useAuth()
  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    dashboardApi
      .getStats()
      .then(setStats)
      .catch((err) => toast.showToast(err?.response?.data?.message || 'Error al cargar las estadísticas', 'error'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="flex items-center justify-center h-64 text-gray-500">Cargando...</div>
  if (!stats) return <div className="flex items-center justify-center h-64 text-gray-500">Sin datos disponibles</div>

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Panel</h1>
        <div className="rounded-lg bg-white px-4 py-2 shadow">
          <span className="text-sm text-gray-500">
            Hola, <span className="font-semibold text-gray-800">{user?.name}</span>
          </span>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
        <KpiCard label="Total de usuarios" value={stats.totalUsers} />
        <KpiCard label="Usuarios activos" value={stats.activeUsers} accent="text-green-600" />
        <KpiCard label="Usuarios inactivos" value={stats.inactiveUsers} accent="text-red-500" />
        <KpiCard label="Total de clientes" value={stats.totalClients} />
        <KpiCard label="Clientes activos" value={stats.activeClients} accent="text-green-600" />
        <KpiCard label="Clientes inactivos" value={stats.inactiveClients} accent="text-red-500" />
        <KpiCard label="Credenciales" value={stats.totalCredentials} />
        <KpiCard label="Roles" value={stats.totalRoles} />
        <KpiCard label="Permisos" value={stats.totalPermissions} />
      </div>

      <div className="mt-6 grid gap-6 lg:grid-cols-2">
        <div className="rounded-lg bg-white p-6 shadow">
          <h2 className="mb-4 text-lg font-semibold text-gray-800">Usuarios por rol</h2>
          <BarList
            items={stats.usersByRole.map((u) => ({ label: u.roleName, value: u.count }))}
            emptyText="No hay usuarios registrados."
          />
        </div>
        <div className="rounded-lg bg-white p-6 shadow">
          <h2 className="mb-4 text-lg font-semibold text-gray-800">Credenciales por cliente</h2>
          <BarList
            items={stats.credentialsByClient.map((c) => ({ label: c.clientName, value: c.count }))}
            emptyText="No hay credenciales registradas."
          />
        </div>
      </div>

      <div className="mt-6 rounded-lg bg-white p-6 shadow">
        <h2 className="mb-4 text-lg font-semibold text-gray-800">Usuarios recientes</h2>
        {stats.recentUsers.length === 0 ? (
          <p className="py-4 text-center text-sm text-gray-400">No hay usuarios registrados.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="border-b bg-gray-50 text-xs uppercase text-gray-500">
                <tr>
                  <th className="px-4 py-3">Nombre</th>
                  <th className="px-4 py-3">Correo</th>
                  <th className="px-4 py-3">Rol</th>
                  <th className="px-4 py-3">Creado</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {stats.recentUsers.map((u) => (
                  <tr key={u.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-medium">{u.name}</td>
                    <td className="px-4 py-3 text-gray-600">{u.email}</td>
                    <td className="px-4 py-3">
                      <span className="rounded-full bg-blue-50 px-2.5 py-0.5 text-xs font-medium text-blue-600">
                        {u.roleName}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-gray-500">{u.createdAt?.slice(0, 10)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
