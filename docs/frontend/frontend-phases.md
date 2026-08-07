# Frontend Development Phases

## Stack
- React 18+ with TypeScript
- Vite (build tool)
- React Router v6 (routing)
- Axios (HTTP client)
- Context API or Zustand (state management)
- Tailwind CSS (styling)

## Phase 1: Project Scaffolding ✅
- [x] Initialize React + TypeScript + Vite project
- [x] Configure Tailwind CSS
- [x] Set up folder structure:
  ```
  src/
    api/        (Axios instance, endpoints)
    components/ (reusable UI components)
    contexts/   (auth context)
    hooks/      (custom hooks)
    layouts/    (layout components)
    pages/      (route pages)
    types/      (TypeScript interfaces)
    utils/      (helpers)
  ```
- [x] Create Axios instance with base URL + interceptors (JWT token)
- [x] Set up React Router with route skeleton

## Phase 2: Authentication Module ✅
- [x] Login page (`/login`)
- [x] Auth context (user state, token storage in localStorage)
- [x] Private route wrapper (redirect if not authenticated)
- [x] Auto-redirect to login on 401

## Phase 3: Layout & Navigation ✅
- [x] Dashboard layout with sidebar
- [x] Navigation menu (Users, Clients, Credentials, Roles, Permissions)
- [x] Header with user info + logout button
- [x] Responsive sidebar (collapsible)

## Phase 4: Users CRUD ✅
- [x] User list page (`/users`) with table
- [x] Create user form (modal)
- [x] Edit user form
- [x] Soft delete (deactivate/reactivate toggle)
- [x] Search/filter by name, email, ID number

## Phase 5: Clients CRUD ✅
- [x] Client list page (`/clients`) with table
- [x] Create/edit client form (modal)
- [x] Soft delete (deactivate/reactivate toggle)
- [x] Search/filter by name, email, ID number

## Phase 6: Credentials CRUD ✅
- [x] Credential list page (`/credentials`) with table
- [x] Create/edit credential form (modal, with client selector)
- [x] Delete credential

## Phase 7: Roles CRUD ✅
- [x] Role list page (`/roles`)
- [x] Create/edit role form
- [x] Assign permissions to role (multi-select UI)

## Phase 8: Permissions CRUD ✅
- [x] Permission list page (`/permissions`)
- [x] Create/edit permission form

## Phase 9: Error Handling & UX ✅
- [x] Toast/notification system (success, error, warning)
- [x] Global error boundary
- [x] Loading skeletons/spinners (componente `Spinner`)
- [x] Empty state components (componente `EmptyState`)
- [x] Confirm dialogs for destructive actions (componente `ConfirmDialog` reemplaza `window.confirm`)

## Phase 10: Polish ✅
- [x] Pagination on list pages (hook `useTable` + componente `Pagination`, 5 páginas)
- [x] Sortable columns (componente `SortableTh`)
- [x] Form validation feedback (hook `useFormErrors` con validación inline)
- [x] Responsive design pass (encabezados con `flex-wrap`, tablas con scroll horizontal)
- [ ] Dark mode (opcional, pendiente)
- [x] Login rediseñado (pantalla dividida, panel de marca + formulario)
