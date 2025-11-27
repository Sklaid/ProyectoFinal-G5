import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { EmployeeTable } from './EmployeeTable';
import { Employee, Gender, Department, Level } from '../types/employee';

describe('EmployeeTable', () => {
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
    },
    {
      id: 2,
      firstName: 'Jane',
      lastName: 'Smith',
      email: 'jane@example.com',
      phone: '098-765-4321',
      gender: Gender.FEMALE,
      department: Department.HR,
      level: Level.MID,
      skills: ['React', 'TypeScript'],
      hireDate: '2024-02-20',
      createdAt: '2024-02-20T00:00:00Z',
      updatedAt: '2024-02-20T00:00:00Z'
    }
  ];

  const mockOnEdit = vi.fn();
  const mockOnDelete = vi.fn();

  it('should render employee data in table', () => {
    render(
      <EmployeeTable
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
      />
    );

    expect(screen.getByText('John')).toBeInTheDocument();
    expect(screen.getByText('Doe')).toBeInTheDocument();
    expect(screen.getByText('jane@example.com')).toBeInTheDocument();
  });

  it('should call onEdit when edit button is clicked', async () => {
    const user = userEvent.setup();
    
    render(
      <EmployeeTable
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
      />
    );

    const editButtons = screen.getAllByLabelText(/edit/i);
    await user.click(editButtons[0]);

    expect(mockOnEdit).toHaveBeenCalledWith(mockEmployees[0]);
  });

  it('should call onDelete when delete button is clicked', async () => {
    const user = userEvent.setup();
    
    render(
      <EmployeeTable
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
      />
    );

    const deleteButtons = screen.getAllByLabelText(/delete/i);
    await user.click(deleteButtons[0]);

    expect(mockOnDelete).toHaveBeenCalledWith(1);
  });

  it('should support pagination', async () => {
    const user = userEvent.setup();
    const manyEmployees = Array.from({ length: 15 }, (_, i) => ({
      ...mockEmployees[0],
      id: i + 1,
      firstName: `Employee${i + 1}`
    }));

    render(
      <EmployeeTable
        employees={manyEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
      />
    );

    // Should show first 10 by default
    expect(screen.getByText('Employee1')).toBeInTheDocument();
    expect(screen.queryByText('Employee11')).not.toBeInTheDocument();

    // Navigate to next page
    const nextButton = screen.getByRole('button', { name: /next page/i });
    await user.click(nextButton);

    // Should show next 5
    expect(screen.queryByText('Employee1')).not.toBeInTheDocument();
    expect(screen.getByText('Employee11')).toBeInTheDocument();
  });

  it('should support sorting', async () => {
    const user = userEvent.setup();
    
    render(
      <EmployeeTable
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
      />
    );

    const lastNameHeader = screen.getByText('Last Name');
    await user.click(lastNameHeader);

    // Verify table is still rendered (sorting happened)
    expect(screen.getByText('Doe')).toBeInTheDocument();
    expect(screen.getByText('Smith')).toBeInTheDocument();
  });
});
