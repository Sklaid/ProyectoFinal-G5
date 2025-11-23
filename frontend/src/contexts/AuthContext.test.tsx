import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, act, waitFor } from '@testing-library/react';
import { AuthProvider, useAuth } from './AuthContext';
import { authService } from '../services/authService';

// Mock the auth service
vi.mock('../services/authService', () => ({
  authService: {
    login: vi.fn(),
    logout: vi.fn().mockResolvedValue(undefined),
    validateToken: vi.fn(),
  },
}));

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should throw error when useAuth is used outside AuthProvider', () => {
    // Suppress console.error for this test
    const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
    
    expect(() => {
      renderHook(() => useAuth());
    }).toThrow('useAuth must be used within an AuthProvider');
    
    consoleError.mockRestore();
  });

  it('should initialize with no user when localStorage is empty', () => {
    const { result } = renderHook(() => useAuth(), {
      wrapper: AuthProvider,
    });

    expect(result.current.user).toBeNull();
    expect(result.current.token).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
  });

  it('should restore user from localStorage on mount', async () => {
    const mockUser = { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER' as const };
    const mockToken = 'test-token';

    localStorage.setItem('token', mockToken);
    localStorage.setItem('user', JSON.stringify(mockUser));

    const { result } = renderHook(() => useAuth(), {
      wrapper: AuthProvider,
    });

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    expect(result.current.user).toEqual(mockUser);
    expect(result.current.token).toBe(mockToken);
    expect(result.current.isAuthenticated).toBe(true);
  });

  it('should handle login successfully', async () => {
    const mockUser = { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER' as const };
    const mockToken = 'new-token';
    const mockResponse = { token: mockToken, user: mockUser };

    vi.mocked(authService.login).mockResolvedValue(mockResponse);

    const { result } = renderHook(() => useAuth(), {
      wrapper: AuthProvider,
    });

    await act(async () => {
      await result.current.login('testuser', 'password123');
    });

    expect(authService.login).toHaveBeenCalledWith({
      username: 'testuser',
      password: 'password123',
    });
    expect(result.current.user).toEqual(mockUser);
    expect(result.current.token).toBe(mockToken);
    expect(result.current.isAuthenticated).toBe(true);
    expect(localStorage.getItem('token')).toBe(mockToken);
    expect(localStorage.getItem('user')).toBe(JSON.stringify(mockUser));
  });

  it('should handle login failure', async () => {
    vi.mocked(authService.login).mockRejectedValue(new Error('Invalid credentials'));

    const { result } = renderHook(() => useAuth(), {
      wrapper: AuthProvider,
    });

    await expect(
      act(async () => {
        await result.current.login('testuser', 'wrongpassword');
      })
    ).rejects.toThrow('Invalid credentials');

    expect(result.current.user).toBeNull();
    expect(result.current.token).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
  });

  it('should handle logout', async () => {
    const mockUser = { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER' as const };
    const mockToken = 'test-token';

    localStorage.setItem('token', mockToken);
    localStorage.setItem('user', JSON.stringify(mockUser));

    const { result } = renderHook(() => useAuth(), {
      wrapper: AuthProvider,
    });

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    act(() => {
      result.current.logout();
    });

    expect(result.current.user).toBeNull();
    expect(result.current.token).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });

  it('should clear invalid user data from localStorage', async () => {
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('user', 'invalid-json');

    const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});

    const { result } = renderHook(() => useAuth(), {
      wrapper: AuthProvider,
    });

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    expect(result.current.user).toBeNull();
    expect(result.current.token).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();

    consoleError.mockRestore();
  });
});
