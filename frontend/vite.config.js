import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // Chuyển tiếp /api sang backend Spring Boot khi chạy local
      '/api': 'http://localhost:8080',
    },
  },
})
