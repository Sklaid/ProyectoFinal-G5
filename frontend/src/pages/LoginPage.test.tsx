import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import LoginPage from './LoginPage';
import { AuthProvider } from '../contexts/AuthContext';
import { authService } from '../services/authService';

// Mock the auth service
vi.mock('../services/authService', () => ({
  authService: {
    login: vi.fn(),
    logout: vi.fn(),
    validateToken: vi.fn(),
  },
}));

// Mock useNavigate
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const renderLoginPage = () => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <LoginPage />
      </AuthProvider>
    </BrowserRouter>
  );
};

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
    mockNavigate.mockClear();
  });

  it('should render login form', () => {
    renderLoginPage();

    expect(screen.getByText('DevOps Platform')).toBeInTheDocument();
    expect(screen.getByText('Sign in to continue')).toBeInTheDocument();
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  });

  it('should display validation errors for empty fields', async () => {
    const user = userEvent.setup();
    renderLoginPage();

    const submitButton = screen.getByRole('button', { name: /sign in/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Username is required')).toBeInTheDocument();
      expect(screen.getByText('Password is required')).toBeInTheDocument();
    });
  });

  it('should display validation error for short username', async () => {
    const user = userEvent.setup();
    renderLoginPage();

    const usernameInput = screen.getByLabelText(/username/i);
    const submitButton = screen.getByRole('button', { name: /sign in/i });
    
    await user.type(usernameInput, 'ab');
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Username must be at least 3 characters')).toBeInTheDocument();
    });
  });

  it('should display validation error for short password', async () => {
    const user = userEvent.setup();
    renderLoginPage();

    const passwordInput = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole('button', { name: /sign in/i });
    
    await user.type(passwordInput, '12345');
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Password must be at least 6 characters')).toBeInTheDocument();
    });
  });

  it('should submit form with valid credentials', async () => {
    const user = userEvent.setup();
    const mockResponse = {
      token: 'test-token',
      user: { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER' as const },
    };

    vi.mocked(authService.login).mockResolvedValue(mockResponse);

    renderLoginPage();

    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole('button', { name: /sign in/i });

    await user.type(usernameInput, 'testuser');
    await user.type(passwordInput, 'password123');
    await user.click(submitButton);

    await waitFor(() => {
      expect(authService.login).toHaveBeenCalledWith({
        username: 'testuser',
        password: 'password123',
      });
      expect(mockNavigate).toHaveBeenCalledWith('/employees');
    });
  });

  it('should display error message on login failure', async () => {
    const user = userEvent.setup();
    const errorMessage = 'Invalid credentials';

    vi.mocked(authService.login).mockRejectedValue({
      response: { data: { message: errorMessage } },
    });

    renderLoginPage();

    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole('button', { name: /sign in/i });

    await user.type(usernameInput, 'testuser');
    await user.type(passwordInput, 'wrongpassword');
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });
  });

  it('should disable submit button while submitting', async () => {
    const user = userEvent.setup();
    
    // Make login take some time
    vi.mocked(authService.login).mockImplementation(
      () => new Promise((resolve) => setTimeout(resolve, 100))
    );

    renderLoginPage();

    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole('button', { name: /sign in/i });

    await user.type(usernameInput, 'testuser');
    await user.type(passwordInput, 'password123');
    await user.click(submitButton);

    expect(screen.getByRole('button', { name: /signing in/i })).toBeDisabled();
  });
});
