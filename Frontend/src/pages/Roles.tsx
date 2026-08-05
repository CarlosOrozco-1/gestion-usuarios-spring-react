import { useEffect, useState } from 'react'
import { rolesApi } from '../api/roles'
import { permissionsApi } from '../api/permissions'
import { Modal } from '../components/Modal'
import { useToast } from '../hooks/useToast'
import type { RoleResponse, RoleRequest, PermissionResponse } from '../types'

const emptyForm: RoleRequest = { name: '', description: '' }

export function Roles() {
  const toast = useToast()
  const [roles, setRoles] = useState<RoleResponse[]>([])
  const [allPermissions, setAllPermissions] = useState<PermissionResponse[]>([])
  const [loading, setLoading] = useState(true)

  const [formModal, setFormModal] = useState(false)
  const [editingRole, setEditingRole] = useState<RoleResponse | null>(null)
  const [form, setForm] = useState<RoleRequest>(emptyForm)
  const [submitting, setSubmitting] = useState(false)

  const [permModal, setPermModal] = useState(false)
  const [permRole, setPermRole] = useState<RoleResponse | null>(null)
  const [selectedPerms, setSelectedPerms] = useState<number[]>([])

  useEffect(() => {
    Promise.all([rolesApi.findAll(), permissionsApi.findAll()])
      .then(([r, p]) => { setRoles(r); setAllPermissions(p) })
      .catch((err) => toast.showToast(err?.response?.data?.message || 'Failed to load roles', 'error'))
      .finally(() => setLoading(false))
  }, [])

  function openCreate() {
    setEditingRole(null)
    setForm(emptyForm)
    setFormModal(true)
  }

  function openEdit(role: RoleResponse) {
    setEditingRole(role)
    setForm({ name: role.name, description: role.description })
    setFormModal(true)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    try {
      if (editingRole) {
        const updated = await rolesApi.update(editingRole.id, form)
        setRoles((prev) => prev.map((r) => (r.id === editingRole.id ? updated : r)))
        toast.showToast('Role updated successfully')
      } else {
        const created = await rolesApi.create(form)
        setRoles((prev) => [...prev, created])
        toast.showToast('Role created successfully')
      }
      setFormModal(false)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'Operation failed', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDelete(role: RoleResponse) {
    if (!window.confirm(`Delete role "${role.name}"? This action cannot be undone.`)) return
    try {
      await rolesApi.delete(role.id)
      setRoles((prev) => prev.filter((r) => r.id !== role.id))
      toast.showToast('Role deleted successfully')
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'Failed to delete role', 'error')
    }
  }

  function openPermModal(role: RoleResponse) {
    setPermRole(role)
    setSelectedPerms(role.permissions.map((p) => p.id))
    setPermModal(true)
  }

  async function handleAssignPerms() {
    if (!permRole) return
    try {
      const updated = await rolesApi.assignPermissions(permRole.id, selectedPerms)
      setRoles((prev) => prev.map((r) => (r.id === permRole.id ? updated : r)))
      toast.showToast('Permissions assigned successfully')
      setPermModal(false)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'Failed to assign permissions', 'error')
    }
  }

  function togglePerm(id: number) {
    setSelectedPerms((prev) =>
      prev.includes(id) ? prev.filter((p) => p !== id) : [...prev, id],
    )
  }

  if (loading) return <div className="flex items-center justify-center h-64 text-gray-500">Loading...</div>

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Roles</h1>
        <button
          onClick={openCreate}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          + Add Role
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg bg-white shadow">
        <table className="w-full text-left text-sm">
          <thead className="border-b bg-gray-50 text-xs uppercase text-gray-500">
            <tr>
              <th className="px-4 py-3">ID</th>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Description</th>
              <th className="px-4 py-3">Permissions</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {roles.map((role) => (
              <tr key={role.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-gray-500">{role.id}</td>
                <td className="px-4 py-3 font-medium">{role.name}</td>
                <td className="px-4 py-3 text-gray-600">{role.description || '-'}</td>
                <td className="px-4 py-3">
                  <button
                    onClick={() => openPermModal(role)}
                    className="rounded bg-gray-100 px-2.5 py-1 text-xs font-medium text-gray-600 hover:bg-gray-200"
                  >
                    {role.permissions.length} permission{role.permissions.length !== 1 ? 's' : ''}
                  </button>
                </td>
                <td className="px-4 py-3">
                  <span
                    className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${
                      role.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                    }`}
                  >
                    {role.status}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEdit(role)}
                      className="rounded bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-600 hover:bg-blue-100"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDelete(role)}
                      className="rounded bg-red-50 px-2.5 py-1 text-xs font-medium text-red-600 hover:bg-red-100"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal open={formModal} onClose={() => setFormModal(false)} title={editingRole ? 'Edit Role' : 'Create Role'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Name</label>
            <input
              required
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Description</label>
            <textarea
              rows={3}
              value={form.description ?? ''}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={() => setFormModal(false)}
              className="rounded-lg border px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700 disabled:opacity-50"
            >
              {submitting ? 'Saving...' : editingRole ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>

      <Modal open={permModal} onClose={() => setPermModal(false)} title={`Permissions for ${permRole?.name ?? ''}`}>
        <div className="space-y-2">
          {allPermissions.map((perm) => (
            <label
              key={perm.id}
              className="flex cursor-pointer items-center gap-3 rounded-lg border px-4 py-3 hover:bg-gray-50"
            >
              <input
                type="checkbox"
                checked={selectedPerms.includes(perm.id)}
                onChange={() => togglePerm(perm.id)}
                className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <div>
                <p className="text-sm font-medium text-gray-800">{perm.name}</p>
                <p className="text-xs text-gray-500">{perm.resourcePath}</p>
              </div>
            </label>
          ))}
          {allPermissions.length === 0 && (
            <p className="text-center text-sm text-gray-400">No permissions available.</p>
          )}
        </div>
        <div className="flex justify-end gap-3 pt-4">
          <button
            onClick={() => setPermModal(false)}
            className="rounded-lg border px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            onClick={handleAssignPerms}
            className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700"
          >
            Save Permissions
          </button>
        </div>
      </Modal>
    </div>
  )
}
