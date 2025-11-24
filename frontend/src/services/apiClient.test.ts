import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { AxiosError, InternalAxiosRequestConfig } from 'axios';
import apiClient from './apiClient';

describe('apiClient', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should be an axios instance', () => {
    expect(apiClient).toBeDefined();
    expect(typeof apiClient.get).toBe('function');
    expect(typeof apiClient.post).toBe('function');
    expect(typeof apiClient.put).toBe('function');
    expect(typeof apiClient.delete).toBe('function');
  });

  it('should have correct default configuration', () => {
    expect(apiClient.defaults.timeout).toBe(10000);
    expect(apiClient.defaults.headers['Content-Type']).toBe('application/json');
  });

  it('should have request and response interceptors configured', () => {
    expect(apiClient.interceptors.request).toBeDefined();
    expect(apiClient.interceptors.response).toBeDefined();
  });

  describe('Request Interceptor', () => {
    it('should attach token to request headers when token exists', () => {
      const token = 'test-token-123';
      localStorage.setItem('token', token);

      const config: InternalAxiosRequestConfig = {
        headers: {} as any,
      } as InternalAxiosRequestConfig;

      const requestInterceptor = apiClient.interceptors.request['handlers'][0];
      const result = requestInterceptor.fulfilled(config);

      expect(result.headers.Authorization).toBe(`Bearer ${token}`);
    });

    it('should not attach Authorization header when token does not exist', () => {
      const config: InternalAxiosRequestConfig = {
        headers: {} as any,
      } as InternalAxiosRequestConfig;

      const requestInterceptor = apiClient.interceptors.request['handlers'][0];
      const result = requestInterceptor.fulfilled(config);

      expect(result.headers.Authorization).toBeUndefined();
    });

    it('should reject on request error', async () => {
      const error = new Error('Request error') as AxiosError;
      const requestInterceptor = apiClient.interceptors.request['handlers'][0];

      await expect(requestInterceptor.rejected(error)).rejects.toThrow('Request error');
    });
  });

  describe('Response Interceptor', () => {
    let consoleErrorSpy: any;
    let originalLocation: Location;

    beforeEach(() => {
      consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
      originalLocation = globalThis.location;
      delete (globalThis as any).location;
      globalThis.location = { ...originalLocation, href: '' } as Location;
    });

    afterEach(() => {
      consoleErrorSpy.mockRestore();
      globalThis.location = originalLocation;
    });

    it('should return response on success', () => {
      const response = { data: { message: 'success' }, status: 200 };
      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      const result = responseInterceptor.fulfilled(response);

      expect(result).toEqual(response);
    });

    it('should handle 401 error - clear storage and redirect to login', async () => {
      localStorage.setItem('token', 'test-token');
      localStorage.setItem('user', JSON.stringify({ id: 1 }));

      const error = {
        response: { status: 401 },
        message: 'Unauthorized',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('user')).toBeNull();
      expect(globalThis.location.href).toBe('/login');
    });

    it('should handle 403 error - log permission error', async () => {
      const error = {
        response: { status: 403 },
        message: 'Forbidden',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(consoleErrorSpy).toHaveBeenCalledWith('You do not have permission to perform this action');
    });

    it('should handle 500 error - log server error', async () => {
      const error = {
        response: { status: 500 },
        message: 'Internal Server Error',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(consoleErrorSpy).toHaveBeenCalledWith('Server error. Please try again later');
    });

    it('should handle 502 error - log server error', async () => {
      const error = {
        response: { status: 502 },
        message: 'Bad Gateway',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(consoleErrorSpy).toHaveBeenCalledWith('Server error. Please try again later');
    });

    it('should handle 503 error - log server error', async () => {
      const error = {
        response: { status: 503 },
        message: 'Service Unavailable',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(consoleErrorSpy).toHaveBeenCalledWith('Server error. Please try again later');
    });

    it('should handle 504 error - log server error', async () => {
      const error = {
        response: { status: 504 },
        message: 'Gateway Timeout',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(consoleErrorSpy).toHaveBeenCalledWith('Server error. Please try again later');
    });

    it('should handle other status codes - log generic error', async () => {
      const error = {
        response: { status: 400 },
        message: 'Bad Request',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(consoleErrorSpy).toHaveBeenCalledWith('An error occurred:', 'Bad Request');
    });

    it('should handle network error - no response received', async () => {
      const error = {
        request: {},
        message: 'Network Error',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(consoleErrorSpy).toHaveBeenCalledWith('Network error. Please check your connection');
    });

    it('should handle generic error - no request or response', async () => {
      const error = {
        message: 'Something went wrong',
      } as AxiosError;

      const responseInterceptor = apiClient.interceptors.response['handlers'][0];

      await expect(responseInterceptor.rejected(error)).rejects.toEqual(error);

      expect(consoleErrorSpy).toHaveBeenCalledWith('An error occurred:', 'Something went wrong');
    });
  });
});
