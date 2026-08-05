import { NavLink } from 'react-router-dom'

const links = [
  { to: '/', label: 'Panel', icon: '📊' },
  { to: '/users', label: 'Usuarios', icon: '👥' },
  { to: '/clients', label: 'Clientes', icon: '🧑‍💼' },
  { to: '/credentials', label: 'Credenciales', icon: '🔑' },
  { to: '/roles', label: 'Roles', icon: '🔐' },
  { to: '/permissions', label: 'Permisos', icon: '⚙️' },
]

interface SidebarProps {
  open: boolean
  onClose: () => void
}

export function Sidebar({ open, onClose }: SidebarProps) {
  return (
    <>
      {open && (
        <div
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={onClose}
        />
      )}
      <aside
        className={`fixed inset-y-0 left-0 z-50 flex w-64 flex-col bg-gray-900 text-white transition-transform duration-200 lg:static lg:translate-x-0 ${
          open ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-16 items-center justify-between border-b border-gray-700 px-4">
          <h1 className="text-lg font-bold">GestionApp</h1>
          <button onClick={onClose} className="text-gray-400 hover:text-white lg:hidden text-xl leading-none">&times;</button>
        </div>
        <nav className="flex-1 space-y-1 p-4">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.to === '/'}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg px-4 py-2.5 text-sm transition-colors ${
                  isActive
                    ? 'bg-blue-600 text-white'
                    : 'text-gray-300 hover:bg-gray-800 hover:text-white'
                }`
              }
            >
              <span>{link.icon}</span>
              {link.label}
            </NavLink>
          ))}
        </nav>
      </aside>
    </>
  )
}
