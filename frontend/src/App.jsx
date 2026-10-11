import { useEffect, useState } from 'react'
import axios from 'axios'

export default function App() {
  const [status, setStatus] = useState('đang kiểm tra...')

  useEffect(() => {
    axios
      .get('/api/health')
      .then((res) => setStatus(res.data.status))
      .catch(() => setStatus('không kết nối được backend'))
  }, [])

  return (
    <main style={{ fontFamily: 'sans-serif', padding: 24 }}>
      <h1>BlueMoon</h1>
      <p>Hệ thống quản lý chung cư</p>
      <p>
        Backend: <strong>{status}</strong>
      </p>
    </main>
  )
}
