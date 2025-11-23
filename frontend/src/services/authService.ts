import apiClient from './apiClient';
import { LoginRequest, AuthResponse } from '../types/auth';

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  },

  logout: async (token: string): Promise<void> => {
    await apiClient.post('/auth/logout', { token });
  },

  validateToken: async (): Promise<boolean> => {
    try {
      await apiClient.get('/auth/validate');
      return true;
    } catch (error) {
      return false;
    }
  },
};
