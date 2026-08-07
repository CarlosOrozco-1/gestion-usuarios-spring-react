import { useMemo, useState } from 'react'

export type SortDir = 'asc' | 'desc'

export interface SortState<T> {
  key: keyof T
  dir: SortDir
}

export function useTable<T extends Record<string, any>>(items: T[], pageSize = 5) {
  const [sort, setSort] = useState<SortState<T> | null>(null)
  const [page, setPage] = useState(1)

  const sorted = useMemo(() => {
    if (!sort) return items
    const { key, dir } = sort
    return [...items].sort((a, b) => {
      const av = a[key]
      const bv = b[key]
      if (av == null && bv == null) return 0
      if (av == null) return 1
      if (bv == null) return -1
      const cmp = typeof av === 'number' && typeof bv === 'number' ? av - bv : String(av).localeCompare(String(bv))
      return dir === 'asc' ? cmp : -cmp
    })
  }, [items, sort])

  const pageCount = Math.max(1, Math.ceil(sorted.length / pageSize))
  const currentPage = Math.min(page, pageCount)
  const rows = useMemo(() => {
    const start = (currentPage - 1) * pageSize
    return sorted.slice(start, start + pageSize)
  }, [sorted, currentPage, pageSize])

  function toggleSort(key: keyof T) {
    setPage(1)
    setSort((prev) =>
      prev?.key === key ? { key, dir: prev.dir === 'asc' ? 'desc' : 'asc' } : { key, dir: 'asc' },
    )
  }

  function goToPage(next: number) {
    setPage(Math.min(Math.max(1, next), pageCount))
  }

  return { rows, page: currentPage, pageCount, total: sorted.length, sort, toggleSort, goToPage }
}
