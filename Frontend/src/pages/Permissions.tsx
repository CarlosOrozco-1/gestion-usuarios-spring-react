import { useEffect, useState } from 'react'
import { permissionsApi } from '../api/permissions'
import { Modal } from '../components/Modal'
import { useToast } from '../hooks/useToast'
import type { PermissionResponse, PermissionRequest } from '../types'

const emptyForm: PermissionRequest = { name: '', description: '', resourcePath: '' }

export function Permissions() {
  const toast = useToast()
  const [permissions, setPermissions] = useState<PermissionResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<PermissionResponse | null>(null)
  const [form, setForm] = useState<PermissionRequest>(emptyForm)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    permissionsApi.findAll()
      .then(setPermissions)
      .catch((err) => toast.showToast(err?.response?.data?.message || 'Failed to load permissions', 'error'))
      .finally(() => setLoading(false))
  }, [])

  function openCreate() {
    setEditing(null)
    setForm(emptyForm)
    setModalOpen(true)
  }

  function openEdit(perm: PermissionResponse) {
    setEditing(perm)
    setForm({ name: perm.name, description: perm.description, resourcePath: perm.resourcePath })
    setModalOpen(true)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    try {
      if (editing) {
        const updated = await permissionsApi.update(editing.id, form)
        setPermissions((prev) => prev.map((p) => (p.id === editing.id ? updated : p)))
        toast.showToast('Permission updated successfully')
      } else {
        const created = await permissionsApi.create(form)
        setPermissions((prev) => [...prev, created])
        toast.showToast('Permission created successfully')
      }
      setModalOpen(false)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'Operation failed', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDelete(perm: PermissionResponse) {
    if (!window.confirm(`Delete permission "${perm.name}"? This action cannot be undone.`)) return
    try {
      await permissionsApi.delete(perm.id)
      setPermissions((prev) => prev.filter((p) => p.id !== perm.id))
      toast.showToast('Permission deleted successfully')
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'Failed to delete permission', 'error')
    }
  }

  if (loading) return <div className="flex items-center justify-center h-64 text-gray-500">Loading...</div>

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Permissions</h1>
        <button
          onClick={openCreate}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          + Add Permission
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg bg-white shadow">
        <table className="w-full text-left text-sm">
          <thead className="border-b bg-gray-50 text-xs uppercase text-gray-500">
            <tr>
              <th className="px-4 py-3">ID</th>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Description</th>
              <th className="px-4 py-3">Resource Path</th>
              <th className="px-4 py-3">Created</th>
              <th className="px-4 py-3">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {permissions.map((perm) => (
              <tr key={perm.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-gray-500">{perm.id}</td>
                <td className="px-4 py-3 font-medium">{perm.name}</td>
                <td className="px-4 py-3 text-gray-600">{perm.description || '-'}</td>
                <td className="px-4 py-3">
                  <code className="rounded bg-gray-100 px-2 py-0.5 text-xs text-gray-600">
                    {perm.resourcePath || '-'}
                  </code>
                </td>
                <td className="px-4 py-3 text-gray-500">{perm.createdAt?.slice(0, 10)}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEdit(perm)}
                      className="rounded bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-600 hover:bg-blue-100"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDelete(perm)}
                      className="rounded bg-red-50 px-2.5 py-1 text-xs font-medium text-red-600 hover:bg-red-100"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {permissions.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-400">
                  No permissions found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit Permission' : 'Create Permission'}>
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
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Resource Path</label>
            <input
              value={form.resourcePath ?? ''}
              onChange={(e) => setForm({ ...form, resourcePath: e.target.value })}
              placeholder="/api/resource"
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={() => setModalOpen(false)}
              className="rounded-lg border px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700 disabled:opacity-50"
            >
              {submitting ? 'Saving...' : editing ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
