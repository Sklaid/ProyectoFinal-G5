import { describe, it, expect, vi, beforeEach } from 'vitest';
import { employeeService } from './employeeService';
import apiClient from './apiClient';
import { Employee, EmployeeFormData, Gender, Department, Level } from '../types/employee';

// Mock the apiClient
vi.mock('./apiClient');

describe('employeeService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getAll', () => {
    it('should fetch all employees successfully', async () => {
      const mockEmployees: Employee[] = [
        {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john@example.com',
          phone: '1234567890',
          gender: Gender.MALE,
          department: Department.IT,
          level: Level.SENIOR,
          skills: ['Java', 'Python'],
          hireDate: '2023-01-01',
          createdAt: '2023-01-01T00:00:00Z',
          updatedAt: '2023-01-01T00:00:00Z'
        },
        {
          id: 2,
          firstName: 'Jane',
          lastName: 'Smith',
          email: 'jane@example.com',
          phone: '0987654321',
          gender: Gender.FEMALE,
          department: Department.HR,
          level: Level.MID,
          skills: ['Communication'],
          hireDate: '2023-02-01',
          createdAt: '2023-02-01T00:00:00Z',
          updatedAt: '2023-02-01T00:00:00Z'
        }
      ];

      vi.mocked(apiClient.get).mockResolvedValue({ data: mockEmployees });

      const result = await employeeService.getAll();

      expect(apiClient.get).toHaveBeenCalledWith('/employees');
      expect(result).toEqual(mockEmployees);
    });

    it('should handle errors when fetching all employees', async () => {
      const mockError = new Error('Network error');
      vi.mocked(apiClient.get).mockRejectedValue(mockError);

      await expect(employeeService.getAll()).rejects.toThrow('Network error');
      expect(apiClient.get).toHaveBeenCalledWith('/employees');
    });
  });

  describe('getById', () => {
    it('should fetch employee by ID successfully', async () => {
      const mockEmployee: Employee = {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '1234567890',
        gender: Gender.MALE,
        department: Department.IT,
        level: Level.SENIOR,
        skills: ['Java', 'Python'],
        hireDate: '2023-01-01',
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: '2023-01-01T00:00:00Z'
      };

      vi.mocked(apiClient.get).mockResolvedValue({ data: mockEmployee });

      const result = await employeeService.getById(1);

      expect(apiClient.get).toHaveBeenCalledWith('/employees/1');
      expect(result).toEqual(mockEmployee);
    });

    it('should handle errors when fetching employee by ID', async () => {
      const mockError = new Error('Employee not found');
      vi.mocked(apiClient.get).mockRejectedValue(mockError);

      await expect(employeeService.getById(999)).rejects.toThrow('Employee not found');
      expect(apiClient.get).toHaveBeenCalledWith('/employees/999');
    });
  });

  describe('create', () => {
    it('should create a new employee successfully', async () => {
      const formData: EmployeeFormData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '1234567890',
        gender: 'MALE',
        department: 'IT',
        level: 'SENIOR',
        skills: ['Java', 'Python'],
        hireDate: '2023-01-01'
      };

      const mockCreatedEmployee: Employee = {
        id: 1,
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone,
        gender: Gender.MALE,
        department: Department.IT,
        level: Level.SENIOR,
        skills: formData.skills,
        hireDate: formData.hireDate,
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: '2023-01-01T00:00:00Z'
      };

      vi.mocked(apiClient.post).mockResolvedValue({ data: mockCreatedEmployee });

      const result = await employeeService.create(formData);

      expect(apiClient.post).toHaveBeenCalledWith('/employees', formData);
      expect(result).toEqual(mockCreatedEmployee);
    });

    it('should handle errors when creating employee', async () => {
      const formData: EmployeeFormData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '1234567890',
        gender: 'MALE',
        department: 'IT',
        level: 'SENIOR',
        skills: ['Java'],
        hireDate: '2023-01-01'
      };

      const mockError = new Error('Validation error');
      vi.mocked(apiClient.post).mockRejectedValue(mockError);

      await expect(employeeService.create(formData)).rejects.toThrow('Validation error');
      expect(apiClient.post).toHaveBeenCalledWith('/employees', formData);
    });
  });

  describe('update', () => {
    it('should update an existing employee successfully', async () => {
      const formData: EmployeeFormData = {
        firstName: 'John',
        lastName: 'Doe Updated',
        email: 'john.updated@example.com',
        phone: '1234567890',
        gender: 'MALE',
        department: 'IT',
        level: 'LEAD',
        skills: ['Java', 'Python', 'TypeScript'],
        hireDate: '2023-01-01'
      };

      const mockUpdatedEmployee: Employee = {
        id: 1,
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone,
        gender: Gender.MALE,
        department: Department.IT,
        level: Level.LEAD,
        skills: formData.skills,
        hireDate: formData.hireDate,
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: '2023-01-01T00:00:00Z'
      };

      vi.mocked(apiClient.put).mockResolvedValue({ data: mockUpdatedEmployee });

      const result = await employeeService.update(1, formData);

      expect(apiClient.put).toHaveBeenCalledWith('/employees/1', formData);
      expect(result).toEqual(mockUpdatedEmployee);
    });

    it('should handle errors when updating employee', async () => {
      const formData: EmployeeFormData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '1234567890',
        gender: 'MALE',
        department: 'IT',
        level: 'SENIOR',
        skills: ['Java'],
        hireDate: '2023-01-01'
      };

      const mockError = new Error('Update failed');
      vi.mocked(apiClient.put).mockRejectedValue(mockError);

      await expect(employeeService.update(1, formData)).rejects.toThrow('Update failed');
      expect(apiClient.put).toHaveBeenCalledWith('/employees/1', formData);
    });
  });

  describe('delete', () => {
    it('should delete an employee successfully', async () => {
      vi.mocked(apiClient.delete).mockResolvedValue({ data: undefined });

      await employeeService.delete(1);

      expect(apiClient.delete).toHaveBeenCalledWith('/employees/1');
    });

    it('should handle errors when deleting employee', async () => {
      const mockError = new Error('Delete failed');
      vi.mocked(apiClient.delete).mockRejectedValue(mockError);

      await expect(employeeService.delete(1)).rejects.toThrow('Delete failed');
      expect(apiClient.delete).toHaveBeenCalledWith('/employees/1');
    });
  });
});
