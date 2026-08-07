import { useEffect, useState } from 'react'
import { permissionsApi } from '../api/permissions'
import { Modal } from '../components/Modal'
import { Spinner } from '../components/Spinner'
import { EmptyState } from '../components/EmptyState'
import { ConfirmDialog } from '../components/ConfirmDialog'
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
  const [deleteTarget, setDeleteTarget] = useState<PermissionResponse | null>(null)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    permissionsApi.findAll()
      .then(setPermissions)
      .catch((err) => toast.showToast(err?.response?.data?.message || 'Error al cargar los permisos', 'error'))
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
        toast.showToast('Permiso actualizado correctamente')
      } else {
        const created = await permissionsApi.create(form)
        setPermissions((prev) => [...prev, created])
        toast.showToast('Permiso creado correctamente')
      }
      setModalOpen(false)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'La operación falló', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  function handleDelete(perm: PermissionResponse) {
    setDeleteTarget(perm)
  }

  async function confirmDelete() {
    if (!deleteTarget) return
    setDeleting(true)
    try {
      await permissionsApi.delete(deleteTarget.id)
      setPermissions((prev) => prev.filter((p) => p.id !== deleteTarget.id))
      toast.showToast('Permiso eliminado correctamente')
      setDeleteTarget(null)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'Error al eliminar el permiso', 'error')
    } finally {
      setDeleting(false)
    }
  }

  if (loading) return <Spinner />

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Permisos</h1>
        <button
          onClick={openCreate}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          + Agregar permiso
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg bg-white shadow">
        <table className="w-full text-left text-sm">
          <thead className="border-b bg-gray-50 text-xs uppercase text-gray-500">
            <tr>
              <th className="px-4 py-3">ID</th>
              <th className="px-4 py-3">Nombre</th>
              <th className="px-4 py-3">Descripción</th>
              <th className="px-4 py-3">Ruta del recurso</th>
              <th className="px-4 py-3">Creado</th>
              <th className="px-4 py-3">Acciones</th>
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
                      Editar
                    </button>
                    <button
                      onClick={() => handleDelete(perm)}
                      className="rounded bg-red-50 px-2.5 py-1 text-xs font-medium text-red-600 hover:bg-red-100"
                    >
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {permissions.length === 0 && <EmptyState message="No se encontraron permisos." colSpan={6} />}
          </tbody>
        </table>
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Editar permiso' : 'Crear permiso'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Nombre</label>
            <input
              required
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Descripción</label>
            <textarea
              rows={3}
              value={form.description ?? ''}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Ruta del recurso</label>
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
              Cancelar
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700 disabled:opacity-50"
            >
              {submitting ? 'Guardando...' : editing ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={deleteTarget !== null}
        title="Eliminar permiso"
        message={deleteTarget ? `¿Eliminar el permiso "${deleteTarget.name}"? Esta acción no se puede deshacer.` : ''}
        loading={deleting}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
