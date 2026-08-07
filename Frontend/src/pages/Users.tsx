import { useEffect, useState } from 'react'
import { usersApi } from '../api/users'
import { rolesApi } from '../api/roles'
import { Modal } from '../components/Modal'
import { Spinner } from '../components/Spinner'
import { EmptyState } from '../components/EmptyState'
import { Pagination } from '../components/Pagination'
import { SortableTh } from '../components/SortableTh'
import { useToast } from '../hooks/useToast'
import { useTable } from '../hooks/useTable'
import { useFormErrors, validators, validateForm } from '../hooks/useFormErrors'
import type { UserResponse, UserRequest, RoleResponse } from '../types'

const emptyForm: UserRequest = { idNumber: '', name: '', email: '', password: '', roleId: 0 }

export function Users() {
  const toast = useToast()
  const [users, setUsers] = useState<UserResponse[]>([])
  const [roles, setRoles] = useState<RoleResponse[]>([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingUser, setEditingUser] = useState<UserResponse | null>(null)
  const [form, setForm] = useState<UserRequest>(emptyForm)
  const [submitting, setSubmitting] = useState(false)
  const { errors, setErrors, clearError } = useFormErrors()

  useEffect(() => {
    Promise.all([usersApi.findAll(), rolesApi.findAll()])
      .then(([u, r]) => { setUsers(u); setRoles(r) })
      .catch((err) => toast.showToast(err?.response?.data?.message || 'Error al cargar los usuarios', 'error'))
      .finally(() => setLoading(false))
  }, [])

  function openCreate() {
    setEditingUser(null)
    setForm(emptyForm)
    setModalOpen(true)
  }

  function openEdit(user: UserResponse) {
    setEditingUser(user)
    const role = roles.find((r) => r.name === user.roleName)
    setForm({ idNumber: user.idNumber, name: user.name, email: user.email, roleId: role?.id ?? 0 })
    setModalOpen(true)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const errs = validateForm(form, {
      idNumber: validators.required(),
      name: validators.required(),
      email: validators.required(),
      ...(!editingUser ? { password: validators.minLength(6) } : {}),
      roleId: (v) => (v ? null : 'Selecciona un rol'),
    })
    if (Object.keys(errs).length > 0) {
      setErrors(errs as Record<string, string>)
      return
    }
    setSubmitting(true)
    try {
      if (editingUser) {
        const updated = await usersApi.update(editingUser.id, form)
        setUsers((prev) => prev.map((u) => (u.id === editingUser.id ? updated : u)))
        toast.showToast('Usuario actualizado correctamente')
      } else {
        const created = await usersApi.create(form)
        setUsers((prev) => [...prev, created])
        toast.showToast('Usuario creado correctamente')
      }
      setModalOpen(false)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'La operación falló', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  async function toggleStatus(user: UserResponse) {
    try {
      if (user.active) {
        await usersApi.deactivate(user.id)
        toast.showToast('Usuario desactivado')
      } else {
        await usersApi.reactivate(user.id)
        toast.showToast('Usuario reactivado')
      }
      setUsers((prev) =>
        prev.map((u) => (u.id === user.id ? { ...u, active: !u.active, status: u.active ? 'INACTIVE' : 'ACTIVE' } : u)),
      )
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'La operación falló', 'error')
    }
  }

  const filtered = users.filter(
    (u) =>
      u.name.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase()) ||
      u.idNumber.toLowerCase().includes(search.toLowerCase()),
  )

  const table = useTable(filtered, 5)

  if (loading) return <Spinner />

  return (
    <div>
      <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold text-gray-800">Usuarios</h1>
        <button
          onClick={openCreate}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          + Agregar usuario
        </button>
      </div>

      <div className="mb-4">
        <input
          type="text"
          placeholder="Buscar por nombre, correo o número de identificación..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full rounded-lg border px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      <div className="overflow-x-auto rounded-lg bg-white shadow">
        <table className="w-full text-left text-sm">
          <thead className="border-b bg-gray-50 text-xs uppercase text-gray-500">
            <tr>
              <SortableTh label="ID" sortKey="id" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="N° Identificación" sortKey="idNumber" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Nombre" sortKey="name" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Correo" sortKey="email" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Rol" sortKey="roleName" sort={table.sort} onSort={table.toggleSort} />
              <th className="px-4 py-3">Estado</th>
              <th className="px-4 py-3">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {table.rows.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-gray-500">{user.id}</td>
                <td className="px-4 py-3 font-medium">{user.idNumber}</td>
                <td className="px-4 py-3">{user.name}</td>
                <td className="px-4 py-3 text-gray-600">{user.email}</td>
                <td className="px-4 py-3">
                  <span className="rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-medium text-blue-700">
                    {user.roleName}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <span
                    className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${
                      user.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                    }`}
                  >
                    {user.status}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEdit(user)}
                      className="rounded bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-600 hover:bg-blue-100"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => toggleStatus(user)}
                      className={`rounded px-2.5 py-1 text-xs font-medium ${
                        user.active
                          ? 'bg-red-50 text-red-600 hover:bg-red-100'
                          : 'bg-green-50 text-green-600 hover:bg-green-100'
                      }`}
                    >
                    {user.active ? 'Desactivar' : 'Reactivar'}
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {filtered.length === 0 && <EmptyState message="No se encontraron usuarios." colSpan={7} />}
          </tbody>
        </table>
        <Pagination page={table.page} pageCount={table.pageCount} total={table.total} pageSize={5} onChange={table.goToPage} />
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editingUser ? 'Editar usuario' : 'Crear usuario'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Número de identificación</label>
            <input
              value={form.idNumber}
              onChange={(e) => { setForm({ ...form, idNumber: e.target.value }); clearError('idNumber') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.idNumber ? 'border-red-400' : ''}`}
            />
            {errors.idNumber && <p className="mt-1 text-xs text-red-600">{errors.idNumber}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Nombre</label>
            <input
              value={form.name}
              onChange={(e) => { setForm({ ...form, name: e.target.value }); clearError('name') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.name ? 'border-red-400' : ''}`}
            />
            {errors.name && <p className="mt-1 text-xs text-red-600">{errors.name}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Correo electrónico</label>
            <input
              type="email"
              value={form.email}
              onChange={(e) => { setForm({ ...form, email: e.target.value }); clearError('email') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.email ? 'border-red-400' : ''}`}
            />
            {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email}</p>}
          </div>
          {!editingUser && (
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Contraseña</label>
              <input
                type="password"
                value={form.password ?? ''}
                onChange={(e) => { setForm({ ...form, password: e.target.value }); clearError('password') }}
                className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.password ? 'border-red-400' : ''}`}
              />
              {errors.password && <p className="mt-1 text-xs text-red-600">{errors.password}</p>}
            </div>
          )}
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Rol</label>
            <select
              value={form.roleId}
              onChange={(e) => { setForm({ ...form, roleId: Number(e.target.value) }); clearError('roleId') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.roleId ? 'border-red-400' : ''}`}
            >
              <option value={0} disabled>Selecciona un rol</option>
              {roles.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.name}
                </option>
              ))}
            </select>
            {errors.roleId && <p className="mt-1 text-xs text-red-600">{errors.roleId}</p>}
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={() => setModalOpen(false)}
              className="rounded-lg border px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700 disabled:opacity-50"
            >
              {submitting ? 'Guardando...' : editingUser ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
