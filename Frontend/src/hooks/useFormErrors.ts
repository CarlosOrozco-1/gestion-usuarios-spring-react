import { useState } from 'react'

export type FieldValidator = (value: any) => string | null

export const validators = {
  required: (msg = 'Este campo es obligatorio'): FieldValidator => (value) =>
    value === undefined || value === null || String(value).trim() === '' ? msg : null,
  email: (msg = 'Correo electrónico inválido'): FieldValidator => (value) => {
    if (!value) return null
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(value)) ? null : msg
  },
  minLength: (min: number, msg?: string): FieldValidator => (value) => {
    if (!value || String(value).length >= min) return null
    return msg ?? `Debe tener al menos ${min} caracteres`
  },
}

export function validateForm<T extends Record<string, any>>(
  form: T,
  rules: Partial<Record<keyof T, FieldValidator>>,
): Partial<Record<keyof T, string>> {
  const errors: Partial<Record<keyof T, string>> = {}
  for (const key of Object.keys(rules) as (keyof T)[]) {
    const validator = rules[key]
    if (!validator) continue
    const msg = validator(form[key])
    if (msg) errors[key] = msg
  }
  return errors
}

export function useFormErrors() {
  const [errors, setErrors] = useState<Record<string, string>>({})

  function clearError(field: string) {
    setErrors((prev) => {
      const next = { ...prev }
      delete next[field]
      return next
    })
  }

  return { errors, setErrors, clearError }
}
