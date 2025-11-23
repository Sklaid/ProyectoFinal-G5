import apiClient from './apiClient';
import { Employee, EmployeeFormData } from '../types/employee';

export const employeeService = {
  /**
   * Get all employees
   */
  async getAll(): Promise<Employee[]> {
    try {
      const response = await apiClient.get<Employee[]>('/employees');
      return response.data;
    } catch (error) {
      console.error('Error fetching employees:', error);
      throw error;
    }
  },

  /**
   * Get employee by ID
   */
  async getById(id: number): Promise<Employee> {
    try {
      const response = await apiClient.get<Employee>(`/employees/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching employee ${id}:`, error);
      throw error;
    }
  },

  /**
   * Create new employee
   */
  async create(data: EmployeeFormData): Promise<Employee> {
    try {
      const response = await apiClient.post<Employee>('/employees', data);
      return response.data;
    } catch (error) {
      console.error('Error creating employee:', error);
      throw error;
    }
  },

  /**
   * Update existing employee
   */
  async update(id: number, data: EmployeeFormData): Promise<Employee> {
    try {
      const response = await apiClient.put<Employee>(`/employees/${id}`, data);
      return response.data;
    } catch (error) {
      console.error(`Error updating employee ${id}:`, error);
      throw error;
    }
  },

  /**
   * Delete employee
   */
  async delete(id: number): Promise<void> {
    try {
      await apiClient.delete(`/employees/${id}`);
    } catch (error) {
      console.error(`Error deleting employee ${id}:`, error);
      throw error;
    }
  }
};
