import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { EmployeeListPage } from './EmployeeListPage';
import { employeeService } from '../services/employeeService';
import { Employee, Gender, Department, Level } from '../types/employee';

// Mock the employee service
vi.mock('../services/employeeService');

// Mock useNavigate
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate
  };
});

describe('EmployeeListPage', () => {
  const mockEmployees: Employee[] = [
    {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '123-456-7890',
      gender: Gender.MALE,
      department: Department.IT,
      level: Level.SENIOR,
      skills: ['Java', 'Spring Boot'],
      hireDate: '2024-01-15',
      createdAt: '2024-01-15T00:00:00Z',
      updatedAt: '2024-01-15T00:00:00Z'
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render loading state initially', () => {
    vi.mocked(employeeService.getAll).mockImplementation(() => new Promise(() => {}));
    
    render(
      <BrowserRouter>
        <EmployeeListPage />
      </BrowserRouter>
    );

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  it('should fetch and display employees', async () => {
    vi.mocked(employeeService.getAll).mockResolvedValue(mockEmployees);

    render(
      <BrowserRouter>
        <EmployeeListPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('John')).toBeInTheDocument();
      expect(screen.getByText('Doe')).toBeInTheDocument();
    });
  });

  it('should navigate to create page when Create New button is clicked', async () => {
    vi.mocked(employeeService.getAll).mockResolvedValue(mockEmployees);
    const user = userEvent.setup();

    render(
      <BrowserRouter>
        <EmployeeListPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('John')).toBeInTheDocument();
    });

    const createButton = screen.getByRole('button', { name: /create new/i });
    await user.click(createButton);

    expect(mockNavigate).toHaveBeenCalledWith('/employees/new');
  });

  it('should handle delete employee', async () => {
    vi.mocked(employeeService.getAll).mockResolvedValue(mockEmployees);
    vi.mocked(employeeService.delete).mockResolvedValue();
    const user = userEvent.setup();

    render(
      <BrowserRouter>
        <EmployeeListPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('John')).toBeInTheDocument();
    });

    const deleteButton = screen.getByLabelText(/delete/i);
    await user.click(deleteButton);

    // Confirm delete in dialog
    const confirmButton = screen.getByRole('button', { name: /delete/i });
    await user.click(confirmButton);

    await waitFor(() => {
      expect(employeeService.delete).toHaveBeenCalledWith(1);
    });
  });
});
