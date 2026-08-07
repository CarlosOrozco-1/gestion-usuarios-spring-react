import { useEffect, useState } from 'react'
import { clientsApi } from '../api/clients'
import { Modal } from '../components/Modal'
import { useToast } from '../hooks/useToast'
import type { ClientResponse, ClientRequest } from '../types'

const emptyForm: ClientRequest = {
  idNumber: '',
  nit: '',
  nombreRazonSocial: '',
  regimenFiscal: '',
  fechaNacimiento: '',
  email: '',
  phone: '',
  address: '',
}

export function Clients() {
  const toast = useToast()
  const [clients, setClients] = useState<ClientResponse[]>([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingClient, setEditingClient] = useState<ClientResponse | null>(null)
  const [form, setForm] = useState<ClientRequest>(emptyForm)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    clientsApi.findAll()
      .then(setClients)
      .catch((err) => toast.showToast(err?.response?.data?.message || 'Error al cargar los clientes', 'error'))
      .finally(() => setLoading(false))
  }, [])

  function openCreate() {
    setEditingClient(null)
    setForm(emptyForm)
    setModalOpen(true)
  }

  function openEdit(client: ClientResponse) {
    setEditingClient(client)
    setForm({
      idNumber: client.idNumber,
      nit: client.nit,
      nombreRazonSocial: client.nombreRazonSocial,
      regimenFiscal: client.regimenFiscal,
      fechaNacimiento: client.fechaNacimiento ? client.fechaNacimiento.slice(0, 10) : '',
      email: client.email,
      phone: client.phone,
      address: client.address,
    })
    setModalOpen(true)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    const payload: ClientRequest = { ...form, fechaNacimiento: form.fechaNacimiento || null }
    try {
      if (editingClient) {
        const updated = await clientsApi.update(editingClient.id, payload)
        setClients((prev) => prev.map((c) => (c.id === editingClient.id ? updated : c)))
        toast.showToast('Cliente actualizado correctamente')
      } else {
        const created = await clientsApi.create(payload)
        setClients((prev) => [...prev, created])
        toast.showToast('Cliente creado correctamente')
      }
      setModalOpen(false)
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'La operación falló', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  async function toggleStatus(client: ClientResponse) {
    try {
      if (client.active) {
        await clientsApi.deactivate(client.id)
        toast.showToast('Cliente desactivado')
      } else {
        await clientsApi.reactivate(client.id)
        toast.showToast('Cliente reactivado')
      }
      setClients((prev) =>
        prev.map((c) => (c.id === client.id ? { ...c, active: !c.active } : c)),
      )
    } catch (err: any) {
      toast.showToast(err?.response?.data?.message || 'La operación falló', 'error')
    }
  }

  const filtered = clients.filter(
    (c) =>
      (c.nombreRazonSocial?.toLowerCase() ?? '').includes(search.toLowerCase()) ||
      (c.nit?.toLowerCase() ?? '').includes(search.toLowerCase()) ||
      c.email.toLowerCase().includes(search.toLowerCase()) ||
      c.idNumber.toLowerCase().includes(search.toLowerCase()),
  )

  if (loading) return <div className="flex items-center justify-center h-64 text-gray-500">Cargando...</div>

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Clientes</h1>
        <button
          onClick={openCreate}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          + Agregar cliente
        </button>
      </div>

      <div className="mb-4">
        <input
          type="text"
          placeholder="Buscar por razón social, NIT, correo o número de identificación..."
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
              <th className="px-4 py-3">N° Identificación</th>
              <th className="px-4 py-3">NIT</th>
              <th className="px-4 py-3">Razón social</th>
              <th className="px-4 py-3">Régimen fiscal</th>
              <th className="px-4 py-3">Fecha nacimiento</th>
              <th className="px-4 py-3">Correo</th>
              <th className="px-4 py-3">Teléfono</th>
              <th className="px-4 py-3">Dirección</th>
              <th className="px-4 py-3">Estado</th>
              <th className="px-4 py-3">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {filtered.map((client) => (
              <tr key={client.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-gray-500">{client.id}</td>
                <td className="px-4 py-3 font-medium">{client.idNumber}</td>
                <td className="px-4 py-3">{client.nit || '-'}</td>
                <td className="px-4 py-3">{client.nombreRazonSocial}</td>
                <td className="px-4 py-3">{client.regimenFiscal || '-'}</td>
                <td className="px-4 py-3">{client.fechaNacimiento ? client.fechaNacimiento.slice(0, 10) : '-'}</td>
                <td className="px-4 py-3 text-gray-600">{client.email}</td>
                <td className="px-4 py-3 text-gray-600">{client.phone || '-'}</td>
                <td className="px-4 py-3 text-gray-600">{client.address || '-'}</td>
                <td className="px-4 py-3">
                  <span
                    className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${
                      client.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                    }`}
                  >
                    {client.active ? 'ACTIVE' : 'INACTIVE'}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEdit(client)}
                      className="rounded bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-600 hover:bg-blue-100"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => toggleStatus(client)}
                      className={`rounded px-2.5 py-1 text-xs font-medium ${
                        client.active
                          ? 'bg-red-50 text-red-600 hover:bg-red-100'
                          : 'bg-green-50 text-green-600 hover:bg-green-100'
                      }`}
                    >
                      {client.active ? 'Desactivar' : 'Reactivar'}
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {filtered.length === 0 && (
              <tr>
                <td colSpan={11} className="px-4 py-8 text-center text-gray-400">
                  No se encontraron clientes.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editingClient ? 'Editar cliente' : 'Crear cliente'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Número de identificación</label>
            <input
              required
              value={form.idNumber}
              onChange={(e) => setForm({ ...form, idNumber: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">NIT</label>
            <input
              required
              value={form.nit}
              onChange={(e) => setForm({ ...form, nit: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Razón social</label>
            <input
              required
              value={form.nombreRazonSocial}
              onChange={(e) => setForm({ ...form, nombreRazonSocial: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Régimen fiscal</label>
            <input
              required
              value={form.regimenFiscal}
              onChange={(e) => setForm({ ...form, regimenFiscal: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Fecha de nacimiento</label>
            <input
              type="date"
              value={form.fechaNacimiento ?? ''}
              onChange={(e) => setForm({ ...form, fechaNacimiento: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Correo electrónico</label>
            <input
              type="email"
              required
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Teléfono</label>
            <input
              value={form.phone ?? ''}
              onChange={(e) => setForm({ ...form, phone: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Dirección</label>
            <textarea
              rows={3}
              value={form.address ?? ''}
              onChange={(e) => setForm({ ...form, address: e.target.value })}
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
              {submitting ? 'Guardando...' : editingClient ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
