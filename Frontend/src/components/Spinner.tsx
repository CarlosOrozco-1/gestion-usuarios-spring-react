interface SpinnerProps {
  message?: string
}

export function Spinner({ message = 'Cargando...' }: SpinnerProps) {
  return (
    <div className="flex h-64 items-center justify-center gap-3 text-gray-500">
      <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
      <span>{message}</span>
    </div>
  )
}
