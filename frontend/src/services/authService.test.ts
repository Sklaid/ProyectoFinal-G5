import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authService } from './authService';
import apiClient from './apiClient';

vi.mock('./apiClient');

describe('authService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('login', () => {
    it('should call apiClient.post with correct credentials and return auth response', async () => {
      const mockResponse = {
        data: {
          token: 'test-token',
          user: { id: 1, username: 'testuser', email: 'test@example.com' }
        }
      };
      
      vi.mocked(apiClient.post).mockResolvedValue(mockResponse);

      const credentials = { username: 'testuser', password: 'password123' };
      const result = await authService.login(credentials);

      expect(apiClient.post).toHaveBeenCalledWith('/auth/login', credentials);
      expect(result).toEqual(mockResponse.data);
    });
  });

  describe('logout', () => {
    it('should call apiClient.post with token', async () => {
      vi.mocked(apiClient.post).mockResolvedValue({});

      const token = 'test-token';
      await authService.logout(token);

      expect(apiClient.post).toHaveBeenCalledWith('/auth/logout', { token });
    });
  });

  describe('validateToken', () => {
    it('should return true when token validation succeeds', async () => {
      vi.mocked(apiClient.get).mockResolvedValue({ data: {} });

      const result = await authService.validateToken();

      expect(apiClient.get).toHaveBeenCalledWith('/auth/validate');
      expect(result).toBe(true);
    });

    it('should return false and log error when token validation fails', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
      const error = new Error('Validation failed');
      vi.mocked(apiClient.get).mockRejectedValue(error);

      const result = await authService.validateToken();

      expect(apiClient.get).toHaveBeenCalledWith('/auth/validate');
      expect(result).toBe(false);
      expect(consoleErrorSpy).toHaveBeenCalledWith('Token validation failed:', error);
      
      consoleErrorSpy.mockRestore();
    });
  });
});
