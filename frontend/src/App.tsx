import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material'
import { AuthProvider } from './contexts/AuthContext'
import LoginPage from './pages/LoginPage'
import { EmployeeListPage } from './pages/EmployeeListPage'
import { EmployeeFormPage } from './pages/EmployeeFormPage'
import PrivateRoute from './components/PrivateRoute'
import { Layout } from './components/Layout'
import { ErrorBoundary } from './components/ErrorBoundary'
import { ToastProvider } from './components/Toast'

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
    <ErrorBoundary>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <BrowserRouter>
          <AuthProvider>
            <ToastProvider>
              <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route
                  path="/employees"
                  element={
                    <PrivateRoute>
                      <Layout>
                        <EmployeeListPage />
                      </Layout>
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/employees/new"
                  element={
                    <PrivateRoute>
                      <Layout>
                        <EmployeeFormPage />
                      </Layout>
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/employees/:id/edit"
                  element={
                    <PrivateRoute>
                      <Layout>
                        <EmployeeFormPage />
                      </Layout>
                    </PrivateRoute>
                  }
                />
                <Route path="/" element={<Navigate to="/employees" replace />} />
              </Routes>
            </ToastProvider>
          </AuthProvider>
        </BrowserRouter>
      </ThemeProvider>
    </ErrorBoundary>
  )
}

export default App
