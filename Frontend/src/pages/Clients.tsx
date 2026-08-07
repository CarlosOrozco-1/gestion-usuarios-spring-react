import { useEffect, useState } from 'react'
import { clientsApi } from '../api/clients'
import { Modal } from '../components/Modal'
import { Spinner } from '../components/Spinner'
import { EmptyState } from '../components/EmptyState'
import { Pagination } from '../components/Pagination'
import { SortableTh } from '../components/SortableTh'
import { useToast } from '../hooks/useToast'
import { useTable } from '../hooks/useTable'
import { useFormErrors, validators, validateForm } from '../hooks/useFormErrors'
import type { ClientResponse, ClientRequest } from '../types'

const emptyForm: ClientRequest = {
  idNumber: '',
  nit: '',
  businessName: '',
  taxRegime: '',
  birthDate: '',
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
  const { errors, setErrors, clearError } = useFormErrors()

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
      businessName: client.businessName,
      taxRegime: client.taxRegime,
      birthDate: client.birthDate ? client.birthDate.slice(0, 10) : '',
      email: client.email,
      phone: client.phone,
      address: client.address,
    })
    setModalOpen(true)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const errs = validateForm(form, {
      idNumber: validators.required(),
      nit: validators.required(),
      businessName: validators.required(),
      taxRegime: validators.required(),
      email: validators.required(),
    })
    if (Object.keys(errs).length > 0) {
      setErrors(errs as Record<string, string>)
      return
    }
    setSubmitting(true)
    const payload: ClientRequest = { ...form, birthDate: form.birthDate || null }
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
      (c.businessName?.toLowerCase() ?? '').includes(search.toLowerCase()) ||
      (c.nit?.toLowerCase() ?? '').includes(search.toLowerCase()) ||
      c.email.toLowerCase().includes(search.toLowerCase()) ||
      c.idNumber.toLowerCase().includes(search.toLowerCase()),
  )

  const table = useTable(filtered, 5)

  if (loading) return <Spinner />

  return (
    <div>
      <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
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
              <SortableTh label="ID" sortKey="id" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="N° Identificación" sortKey="idNumber" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="NIT" sortKey="nit" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Razón social" sortKey="businessName" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Régimen fiscal" sortKey="taxRegime" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Fecha nacimiento" sortKey="birthDate" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Correo" sortKey="email" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Teléfono" sortKey="phone" sort={table.sort} onSort={table.toggleSort} />
              <SortableTh label="Dirección" sortKey="address" sort={table.sort} onSort={table.toggleSort} />
              <th className="px-4 py-3">Estado</th>
              <th className="px-4 py-3">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {table.rows.map((client) => (
              <tr key={client.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-gray-500">{client.id}</td>
                <td className="px-4 py-3 font-medium">{client.idNumber}</td>
                <td className="px-4 py-3">{client.nit || '-'}</td>
                <td className="px-4 py-3">{client.businessName}</td>
                <td className="px-4 py-3">{client.taxRegime || '-'}</td>
                <td className="px-4 py-3">{client.birthDate ? client.birthDate.slice(0, 10) : '-'}</td>
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
            {filtered.length === 0 && <EmptyState message="No se encontraron clientes." colSpan={11} />}
          </tbody>
        </table>
        <Pagination page={table.page} pageCount={table.pageCount} total={table.total} pageSize={5} onChange={table.goToPage} />
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editingClient ? 'Editar cliente' : 'Crear cliente'}>
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
            <label className="mb-1 block text-sm font-medium text-gray-700">NIT</label>
            <input
              value={form.nit}
              onChange={(e) => { setForm({ ...form, nit: e.target.value }); clearError('nit') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.nit ? 'border-red-400' : ''}`}
            />
            {errors.nit && <p className="mt-1 text-xs text-red-600">{errors.nit}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Razón social</label>
            <input
              value={form.businessName}
              onChange={(e) => { setForm({ ...form, businessName: e.target.value }); clearError('businessName') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.businessName ? 'border-red-400' : ''}`}
            />
            {errors.businessName && <p className="mt-1 text-xs text-red-600">{errors.businessName}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Régimen fiscal</label>
            <input
              value={form.taxRegime}
              onChange={(e) => { setForm({ ...form, taxRegime: e.target.value }); clearError('taxRegime') }}
              className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.taxRegime ? 'border-red-400' : ''}`}
            />
            {errors.taxRegime && <p className="mt-1 text-xs text-red-600">{errors.taxRegime}</p>}
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Fecha de nacimiento</label>
            <input
              type="date"
              value={form.birthDate ?? ''}
              onChange={(e) => setForm({ ...form, birthDate: e.target.value })}
              className="w-full rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
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
