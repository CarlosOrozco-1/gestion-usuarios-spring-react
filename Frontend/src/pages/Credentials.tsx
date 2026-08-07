import { useEffect, useState } from 'react'
import { credentialsApi } from '../api/credentials'
import { clientsApi } from '../api/clients'
import { Modal } from '../components/Modal'
import { Spinner } from '../components/Spinner'
import { EmptyState } from '../components/EmptyState'
import { Pagination } from '../components/Pagination'
import { SortableTh } from '../components/SortableTh'
import { ConfirmDialog } from '../components/ConfirmDialog'
import { useToast } from '../hooks/useToast'
import { useTable } from '../hooks/useTable'
import { useFormErrors, validators, validateForm } from '../hooks/useFormErrors'
import type { CredentialResponse, CredentialRequest, ClientResponse } from '../types'

const emptyForm: CredentialRequest = {
  clientId: 0,
  systemName: '',
  username: '',
  encryptedPassword: '',
  url: '',
  notes: '',
}

export function Credentials() {
  const toast = useToast()
  const [credentials, setCredentials] = useState<CredentialResponse[]>([])
  const [clients, setClients] = useState<ClientResponse[]>([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingCred, setEditingCred] = useState<CredentialResponse | null>(null)
  const [form, setForm] = useState<CredentialRequest>(emptyForm)
  const [submitting, setSubmitting] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState<CredentialResponse | null>(null)
  const [deleting, setDeleting] = useState(false)
  const { errors, setErrors, clearError } = useFormErrors()

  useEffect(() => {
    Promise.all([credentialsApi.findAll(), clientsApi.findAll()])
      .then(([c, cl]) => { setCredentials(c); setClients(cl) })
      .catch((err) => toast.showToast(err?.response?.data?.message || 'Error al cargar las credenciales', 'error'))
      .finally(() => setLoading(false))
  }, [])

  function openCreate() {
    setEditingCred(null)
    setForm(emptyForm)
    setModalOpen(true)
  }

  function openEdit(cred: CredentialResponse) {
    setEditingCred(cred)
    setForm({
      clientId: cred.clientId,
      systemName: cred.systemName,
      username: cred.username,
      encryptedPassword: cred.encryptedPassword,
      url: cred.url,
      notes: cred.notes,
    })
    setModalOpen(true)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const errs = validateForm(form, {
      clientId: (v) => (v ? null : 'Selecciona un cliente'),
      systemName: validators.required(),
      username: validators.required(),
      encryptedPassword: validators.required(),
    })
    if (Object.keys(errs).length > 0) {
      setErrors(errs as Record<string, string>)
      return
    }
    setSubmitting(true)
    try {
      if (editingCred) {
        const updated = await credentialsApi.update(editingCred.id, form)
        setCredentials((prev) => prev.map((c) => (c.id === editingCred.id ? updated : c)))
        toast.showToast('Credencial actualizada correctamente')
      } else {
        const created = await credentialsApi.create(form)
        setCredentials((prev) => [...prev, created])
        toast.showToast('Credencial creada correctamente')
      }
      setModalOpen(false)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'La operación falló', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  function handleDelete(cred: CredentialResponse) {
    setDeleteTarget(cred)
  }

  async function confirmDelete() {
    if (!deleteTarget) return
    setDeleting(true)
    try {
      await credentialsApi.delete(deleteTarget.id)
      setCredentials((prev) => prev.filter((c) => c.id !== deleteTarget.id))
      toast.showToast('Credencial eliminada correctamente')
      setDeleteTarget(null)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'Error al eliminar la credencial', 'error')
    } finally {
      setDeleting(false)
    }
  }

  const filtered = credentials.filter(
    (c) =>
      c.systemName.toLowerCase().includes(search.toLowerCase()) ||
      c.username.toLowerCase().includes(search.toLowerCase()) ||
      c.clientName.toLowerCase().includes(search.toLowerCase()),
  )

  const table = useTable(filtered, 5)

  if (loading) return <Spinner />

  return (
    <div>
      <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold text-gray-800">Credenciales</h1>
        <button
          onClick={openCreate}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          + Agregar credencial
        </button>
      </div>

      <div className="mb-4">
        <input
          type="text"
          placeholder="Buscar por sistema, usuario o cliente..."
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
              <SortableTh label="Cliente" sortKey="clientName" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Sistema" sortKey="systemName" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Usuario" sortKey="username" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="URL" sortKey="url" sort={table.sort} onSort={table.toggleSort} />
              <th className="px-4 py-3">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {table.rows.map((cred) => (
              <tr key={cred.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-gray-500">{cred.id}</td>
                <td className="px-4 py-3 font-medium">{cred.clientName}</td>
                <td className="px-4 py-3">{cred.systemName}</td>
                <td className="px-4 py-3 text-gray-600">{cred.username}</td>
                <td className="px-4 py-3 text-gray-600">
                  {cred.url ? (
                    <a href={cred.url} target="_blank" rel="noreferrer" className="text-blue-600 hover:underline">
                      {cred.url}
                    </a>
                  ) : (
                    '-'
                  )}
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEdit(cred)}
                      className="rounded bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-600 hover:bg-blue-100"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => handleDelete(cred)}
                      className="rounded bg-red-50 px-2.5 py-1 text-xs font-medium text-red-600 hover:bg-red-100"
                    >
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {filtered.length === 0 && <EmptyState message="No se encontraron credenciales." colSpan={6} />}
          </tbody>
        </table>
        <Pagination page={table.page} pageCount={table.pageCount} total={table.total} pageSize={5} onChange={table.goToPage} />
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editingCred ? 'Editar credencial' : 'Crear credencial'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Cliente</label>
            <select
              value={form.clientId}
              onChange={(e) => { setForm({ ...form, clientId: Number(e.target.value) }); clearError('clientId') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.clientId ? 'border-red-400' : ''}`}
            >
              <option value={0} disabled>Selecciona un cliente</option>
              {clients.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.businessName}
                </option>
              ))}
            </select>
            {errors.clientId && <p className="mt-1 text-xs text-red-600">{errors.clientId}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Sistema</label>
            <input
              value={form.systemName}
              onChange={(e) => { setForm({ ...form, systemName: e.target.value }); clearError('systemName') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.systemName ? 'border-red-400' : ''}`}
            />
            {errors.systemName && <p className="mt-1 text-xs text-red-600">{errors.systemName}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Usuario</label>
            <input
              value={form.username}
              onChange={(e) => { setForm({ ...form, username: e.target.value }); clearError('username') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.username ? 'border-red-400' : ''}`}
            />
            {errors.username && <p className="mt-1 text-xs text-red-600">{errors.username}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Contraseña</label>
            <input
              value={form.encryptedPassword}
              onChange={(e) => { setForm({ ...form, encryptedPassword: e.target.value }); clearError('encryptedPassword') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.encryptedPassword ? 'border-red-400' : ''}`}
            />
            {errors.encryptedPassword && <p className="mt-1 text-xs text-red-600">{errors.encryptedPassword}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">URL</label>
            <input
              value={form.url ?? ''}
              onChange={(e) => setForm({ ...form, url: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Notas</label>
            <textarea
              rows={3}
              value={form.notes ?? ''}
              onChange={(e) => setForm({ ...form, notes: e.target.value })}
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
              {submitting ? 'Guardando...' : editingCred ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={deleteTarget !== null}
        title="Eliminar credencial"
        message={deleteTarget ? `¿Eliminar la credencial de "${deleteTarget.systemName}"? Esta acción no se puede deshacer.` : ''}
        loading={deleting}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
