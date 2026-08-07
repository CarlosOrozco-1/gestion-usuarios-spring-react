interface EmptyStateProps {
  message: string
  colSpan: number
}

export function EmptyState({ message, colSpan }: EmptyStateProps) {
  return (
    <tr>
      <td colSpan={colSpan} className="px-4 py-8 text-center text-gray-400">
        {message}
      </td>
    </tr>
  )
}
