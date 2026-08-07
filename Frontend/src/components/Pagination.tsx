interface PaginationProps {
  page: number
  pageCount: number
  total: number
  pageSize: number
  onChange: (page: number) => void
}

export function Pagination({ page, pageCount, total, pageSize, onChange }: PaginationProps) {
  if (pageCount <= 1) return null

  const start = (page - 1) * pageSize + 1
  const end = Math.min(page * pageSize, total)

  const pages: (number | '...')[] = []
  if (pageCount <= 7) {
    for (let i = 1; i <= pageCount; i++) pages.push(i)
  } else {
    const edges = [1, pageCount]
    const around = [page - 1, page, page + 1].filter((p) => p >= 2 && p <= pageCount - 1)
    const nums = Array.from(new Set([...edges, ...around])).sort((a, b) => a - b)
    let prev = 0
    for (const n of nums) {
      if (n - prev > 1) pages.push('...')
      pages.push(n)
      prev = n
    }
  }

  const baseBtn = 'rounded-lg border px-3 py-1.5 text-sm disabled:cursor-not-allowed disabled:opacity-50'

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 border-t px-4 py-3">
      <p className="text-sm text-gray-500">
        Mostrando {start}–{end} de {total}
      </p>
      <div className="flex items-center gap-1.5">
        <button onClick={() => onChange(page - 1)} disabled={page <= 1} className={baseBtn}>
          Anterior
        </button>
        {pages.map((p, i) =>
          p === '...' ? (
            <span key={`e${i}`} className="px-1 text-sm text-gray-400">
              …
            </span>
          ) : (
            <button
              key={p}
              onClick={() => onChange(p)}
              className={`${baseBtn} ${
                p === page ? 'border-blue-600 bg-blue-600 text-white' : 'text-gray-700 hover:bg-gray-50'
              }`}
            >
              {p}
            </button>
          ),
        )}
        <button onClick={() => onChange(page + 1)} disabled={page >= pageCount} className={baseBtn}>
          Siguiente
        </button>
      </div>
    </div>
  )
}
