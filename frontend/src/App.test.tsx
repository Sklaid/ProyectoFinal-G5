import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import App from './App';

// Mock all the components and contexts
vi.mock('./contexts/AuthContext', () => ({
  AuthProvider: ({ children }: { children: React.ReactNode }) => <div data-testid="auth-provider">{children}</div>,
  useAuth: () => ({
    user: null,
    login: vi.fn(),
    logout: vi.fn(),
    isLoading: false,
  }),
}));

vi.mock('./components/Toast', () => ({
  ToastProvider: ({ children }: { children: React.ReactNode }) => <div data-testid="toast-provider">{children}</div>,
  useToast: () => ({
    showToast: vi.fn(),
  }),
}));

vi.mock('./components/ErrorBoundary', () => ({
  ErrorBoundary: ({ children }: { children: React.ReactNode }) => <div data-testid="error-boundary">{children}</div>,
}));

vi.mock('./components/Layout', () => ({
  Layout: ({ children }: { children: React.ReactNode }) => <div data-testid="layout">{children}</div>,
}));

vi.mock('./components/PrivateRoute', () => ({
  default: ({ children }: { children: React.ReactNode }) => <div data-testid="private-route">{children}</div>,
}));

vi.mock('./pages/LoginPage', () => ({
  default: () => <div data-testid="login-page">Login Page</div>,
}));

vi.mock('./pages/EmployeeListPage', () => ({
  EmployeeListPage: () => <div data-testid="employee-list-page">Employee List Page</div>,
}));

vi.mock('./pages/EmployeeFormPage', () => ({
  EmployeeFormPage: () => <div data-testid="employee-form-page">Employee Form Page</div>,
}));

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render without crashing', () => {
    render(<App />);
    expect(screen.getByTestId('error-boundary')).toBeInTheDocument();
  });

  it('should wrap app with ErrorBoundary', () => {
    render(<App />);
    expect(screen.getByTestId('error-boundary')).toBeInTheDocument();
  });

  it('should wrap app with AuthProvider', () => {
    render(<App />);
    expect(screen.getByTestId('auth-provider')).toBeInTheDocument();
  });

  it('should wrap app with ToastProvider', () => {
    render(<App />);
    expect(screen.getByTestId('toast-provider')).toBeInTheDocument();
  });

  it('should render login page on /login route', async () => {
    window.history.pushState({}, 'Login', '/login');
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByTestId('login-page')).toBeInTheDocument();
    });
  });

  it('should render employee list page on /employees route', async () => {
    window.history.pushState({}, 'Employees', '/employees');
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByTestId('private-route')).toBeInTheDocument();
      expect(screen.getByTestId('layout')).toBeInTheDocument();
      expect(screen.getByTestId('employee-list-page')).toBeInTheDocument();
    });
  });

  it('should render employee form page on /employees/new route', async () => {
    window.history.pushState({}, 'New Employee', '/employees/new');
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByTestId('private-route')).toBeInTheDocument();
      expect(screen.getByTestId('layout')).toBeInTheDocument();
      expect(screen.getByTestId('employee-form-page')).toBeInTheDocument();
    });
  });

  it('should render employee form page on /employees/:id/edit route', async () => {
    window.history.pushState({}, 'Edit Employee', '/employees/123/edit');
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByTestId('private-route')).toBeInTheDocument();
      expect(screen.getByTestId('layout')).toBeInTheDocument();
      expect(screen.getByTestId('employee-form-page')).toBeInTheDocument();
    });
  });

  it('should redirect from root to /employees', async () => {
    window.history.pushState({}, 'Root', '/');
    render(<App />);
    
    await waitFor(() => {
      expect(window.location.pathname).toBe('/employees');
    });
  });

  it('should have correct provider hierarchy', () => {
    render(<App />);
    
    // ErrorBoundary should be the outermost wrapper
    const errorBoundary = screen.getByTestId('error-boundary');
    expect(errorBoundary).toBeInTheDocument();
    
    // AuthProvider should be inside ErrorBoundary
    const authProvider = screen.getByTestId('auth-provider');
    expect(errorBoundary).toContainElement(authProvider);
    
    // ToastProvider should be inside AuthProvider
    const toastProvider = screen.getByTestId('toast-provider');
    expect(authProvider).toContainElement(toastProvider);
  });

  it('should protect employee routes with PrivateRoute', async () => {
    window.history.pushState({}, 'Employees', '/employees');
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByTestId('private-route')).toBeInTheDocument();
    });
  });

  it('should wrap protected routes with Layout', async () => {
    window.history.pushState({}, 'Employees', '/employees');
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByTestId('layout')).toBeInTheDocument();
    });
  });

  it('should not wrap login page with Layout', async () => {
    window.history.pushState({}, 'Login', '/login');
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByTestId('login-page')).toBeInTheDocument();
      expect(screen.queryByTestId('layout')).not.toBeInTheDocument();
    });
  });

  it('should not protect login route with PrivateRoute', async () => {
    window.history.pushState({}, 'Login', '/login');
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByTestId('login-page')).toBeInTheDocument();
      expect(screen.queryByTestId('private-route')).not.toBeInTheDocument();
    });
  });
});
