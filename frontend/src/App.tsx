import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material'
import { AuthProvider } from './contexts/AuthContext'
import LoginPage from './pages/LoginPage'
import PrivateRoute from './components/PrivateRoute'

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
})

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/employees"
              element={
                <PrivateRoute>
                  <div>
                    <h1>Employees Page (Protected)</h1>
                    <p>This is a protected route</p>
                  </div>
                </PrivateRoute>
              }
            />
            <Route path="/" element={<Navigate to="/login" replace />} />
          </Routes>
        </AuthProvider>
      </BrowserRouter>
    </ThemeProvider>
  )
}

export default App
