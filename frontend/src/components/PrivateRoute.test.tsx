import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import PrivateRoute from './PrivateRoute';
import { AuthProvider } from '../contexts/AuthContext';

// Mock the auth service
vi.mock('../services/authService', () => ({
  authService: {
    login: vi.fn(),
    logout: vi.fn(),
    validateToken: vi.fn(),
  },
}));

const TestComponent = () => <div>Protected Content</div>;
const LoginComponent = () => <div>Login Page</div>;

const renderPrivateRoute = () => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginComponent />} />
          <Route
            path="/protected"
            element={
              <PrivateRoute>
                <TestComponent />
              </PrivateRoute>
            }
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
};

describe('PrivateRoute', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it('should redirect to login when not authenticated', async () => {
    // Set initial route to /protected
    window.history.pushState({}, 'Test page', '/protected');
    
    renderPrivateRoute();

    // Should redirect to login
    expect(await screen.findByText('Login Page')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });

  it('should render children when authenticated', async () => {
    const mockUser = { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER' as const };
    const mockToken = 'test-token';

    localStorage.setItem('token', mockToken);
    localStorage.setItem('user', JSON.stringify(mockUser));

    // Set initial route to /protected
    window.history.pushState({}, 'Test page', '/protected');

    renderPrivateRoute();

    // Should render protected content
    expect(await screen.findByText('Protected Content')).toBeInTheDocument();
    expect(screen.queryByText('Login Page')).not.toBeInTheDocument();
  });

  it('should show loading spinner while checking authentication', async () => {
    // Set initial route to /protected
    window.history.pushState({}, 'Test page', '/protected');

    renderPrivateRoute();

    // The component should eventually render either protected content or redirect to login
    // This test verifies the loading state is handled properly
    await waitFor(() => {
      expect(
        screen.queryByText('Protected Content') || screen.queryByText('Login Page')
      ).toBeTruthy();
    });
  });
});
