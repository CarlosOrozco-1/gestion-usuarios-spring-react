import type { SortState } from '../hooks/useTable'

interface SortableThProps<T> {
  label: string
  sortKey: keyof T
  sort: SortState<T> | null
  onSort: (key: keyof T) => void
}

export function SortableTh<T extends Record<string, any>>({ label, sortKey, sort, onSort }: SortableThProps<T>) {
  const active = sort?.key === sortKey
  return (
    <th className="px-4 py-3">
      <button
        type="button"
        onClick={() => onSort(sortKey)}
        className={`inline-flex items-center gap-1 transition-colors ${
          active ? 'text-blue-600' : 'text-gray-500 hover:text-gray-700'
        }`}
      >
        {label}
        <svg className={`h-3 w-3 ${active ? 'opacity-100' : 'opacity-40'}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
          {!active && <path strokeLinecap="round" strokeLinejoin="round" d="M7 10l5-5 5 5M7 14l5 5 5-5" />}
          {active && sort?.dir === 'asc' && <path strokeLinecap="round" strokeLinejoin="round" d="M12 19V5M5 12l7-7 7 7" />}
          {active && sort?.dir === 'desc' && <path strokeLinecap="round" strokeLinejoin="round" d="M12 5v14M5 12l7 7 7-7" />}
        </svg>
      </button>
    </th>
  )
}
