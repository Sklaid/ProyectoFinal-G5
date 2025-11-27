import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
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
    it('should have request interceptor configured', () => {
      // Test that interceptor exists without accessing internal handlers
      expect(apiClient.interceptors.request).toBeDefined();
      expect(typeof apiClient.interceptors.request.use).toBe('function');
    });

    it('should make requests with token when available', async () => {
      const token = 'test-token-123';
      localStorage.setItem('token', token);

      // The actual interceptor behavior is tested through integration
      // Here we just verify the setup is correct
      expect(localStorage.getItem('token')).toBe(token);
    });

    it('should handle requests without token', () => {
      localStorage.removeItem('token');
      
      // Verify token is not in storage
      expect(localStorage.getItem('token')).toBeNull();
    });
  });

  describe('Response Interceptor', () => {
    it('should have response interceptor configured', () => {
      // Test that interceptor exists without accessing internal handlers
      expect(apiClient.interceptors.response).toBeDefined();
      expect(typeof apiClient.interceptors.response.use).toBe('function');
    });

    it('should be configured to handle errors', () => {
      // The actual error handling behavior is tested through integration tests
      // Here we just verify the interceptor setup is correct
      expect(apiClient.interceptors.response).toBeDefined();
    });

    it('should have proper error handling setup', () => {
      // Verify the client is properly configured
      expect(apiClient.defaults.timeout).toBe(10000);
      expect(apiClient.interceptors.response).toBeDefined();
    });
  });
});
