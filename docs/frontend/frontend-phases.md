# Frontend Development Phases

## Stack
- React 18+ with TypeScript
- Vite (build tool)
- React Router v6 (routing)
- Axios (HTTP client)
- Context API or Zustand (state management)
- Tailwind CSS (styling)

## Phase 1: Project Scaffolding
- [ ] Initialize React + TypeScript + Vite project
- [ ] Configure Tailwind CSS
- [ ] Set up folder structure:
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
- [ ] Create Axios instance with base URL + interceptors (JWT token)
- [ ] Set up React Router with route skeleton

## Phase 2: Authentication Module
- [ ] Login page (`/login`)
- [ ] Auth context (user state, token storage in localStorage)
- [ ] Private route wrapper (redirect if not authenticated)
- [ ] Auto-redirect to login on 401

## Phase 3: Layout & Navigation
- [ ] Dashboard layout with sidebar
- [ ] Navigation menu (Users, Roles, Permissions)
- [ ] Header with user info + logout button
- [ ] Responsive sidebar (collapsible)

## Phase 4: Users CRUD
- [ ] User list page (`/users`) with table
- [ ] Create user form (modal or page)
- [ ] Edit user form
- [ ] Soft delete (deactivate/reactivate toggle)
- [ ] Search/filter by name, email, role

## Phase 5: Roles CRUD
- [ ] Role list page (`/roles`)
- [ ] Create/edit role form
- [ ] Assign permissions to role (multi-select UI)

## Phase 6: Permissions CRUD
- [ ] Permission list page (`/permissions`)
- [ ] Create/edit permission form

## Phase 7: Error Handling & UX
- [ ] Global error boundary
- [ ] Toast/notification system (success, error, warning)
- [ ] Loading skeletons/spinners
- [ ] Empty state components
- [ ] Confirm dialogs for destructive actions

## Phase 8: Polish
- [ ] Pagination on list pages
- [ ] Sortable columns
- [ ] Form validation feedback
- [ ] Responsive design pass
- [ ] Dark mode (optional)
