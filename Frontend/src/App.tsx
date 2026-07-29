import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<div className="p-8 text-center text-xl">Login page — coming soon</div>} />
        <Route path="/" element={<div className="p-8 text-center text-xl">Dashboard — coming soon</div>} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
