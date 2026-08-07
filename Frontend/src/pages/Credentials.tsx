import { useEffect, useState } from 'react'
import { credentialsApi } from '../api/credentials'
import { clientsApi } from '../api/clients'
import { Modal } from '../components/Modal'
import { useToast } from '../hooks/useToast'
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

  async function handleDelete(cred: CredentialResponse) {
    if (!window.confirm(`¿Eliminar la credencial de "${cred.systemName}"? Esta acción no se puede deshacer.`)) return
    try {
      await credentialsApi.delete(cred.id)
      setCredentials((prev) => prev.filter((c) => c.id !== cred.id))
      toast.showToast('Credencial eliminada correctamente')
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'Error al eliminar la credencial', 'error')
    }
  }

  const filtered = credentials.filter(
    (c) =>
      c.systemName.toLowerCase().includes(search.toLowerCase()) ||
      c.username.toLowerCase().includes(search.toLowerCase()) ||
      c.clientName.toLowerCase().includes(search.toLowerCase()),
  )

  if (loading) return <div className="flex items-center justify-center h-64 text-gray-500">Cargando...</div>

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
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
              <th className="px-4 py-3">ID</th>
              <th className="px-4 py-3">Cliente</th>
              <th className="px-4 py-3">Sistema</th>
              <th className="px-4 py-3">Usuario</th>
              <th className="px-4 py-3">URL</th>
              <th className="px-4 py-3">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {filtered.map((cred) => (
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
            {filtered.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-400">
                  No se encontraron credenciales.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editingCred ? 'Editar credencial' : 'Crear credencial'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Cliente</label>
            <select
              required
              value={form.clientId}
              onChange={(e) => setForm({ ...form, clientId: Number(e.target.value) })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value={0} disabled>Selecciona un cliente</option>
              {clients.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombreRazonSocial}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Sistema</label>
            <input
              required
              value={form.systemName}
              onChange={(e) => setForm({ ...form, systemName: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Usuario</label>
            <input
              required
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Contraseña</label>
            <input
              required
              value={form.encryptedPassword}
              onChange={(e) => setForm({ ...form, encryptedPassword: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
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
    </div>
  )
}
