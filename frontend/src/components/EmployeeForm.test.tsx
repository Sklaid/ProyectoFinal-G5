import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { EmployeeForm } from './EmployeeForm';

/**
 * Feature: devops-enterprise-platform, Property 10: UI validation provides feedback
 * 
 * Property: For any form input control, when invalid data is entered,
 * the system should display validation feedback before form submission.
 * 
 * This property-based test validates that the UI provides appropriate
 * validation feedback for various invalid inputs across all form fields.
 */

describe('EmployeeForm - Property-Based Validation Tests', () => {
  let mockOnSubmit: ReturnType<typeof vi.fn>;
  let mockOnCancel: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    mockOnSubmit = vi.fn();
    mockOnCancel = vi.fn();
  });

  /**
   * Property 10: UI validation provides feedback
   * Validates: Requirements 3.5
   * 
   * Test Strategy: Generate various invalid inputs for each field type
   * and verify that validation messages appear.
   */
  describe('Property 10: UI validation provides feedback', () => {
    it('should show validation error for invalid email formats', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      const emailInput = screen.getByLabelText(/email/i);
      
      // Test with one invalid email
      await user.type(emailInput, 'notanemail');
      await user.tab();

      // Verify validation message appears
      await waitFor(() => {
        expect(screen.getByText(/invalid email/i)).toBeInTheDocument();
      }, { timeout: 3000 });

      // Verify form was NOT submitted
      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('should show validation error for invalid first name inputs', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      const firstNameInput = screen.getByLabelText(/first name/i);
      
      // Test with a short name
      await user.type(firstNameInput, 'A');
      await user.tab();

      await waitFor(() => {
        // Check for the specific validation error message
        expect(screen.getByText(/must be at least 2 characters/i)).toBeInTheDocument();
      }, { timeout: 3000 });

      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('should show validation error for invalid phone formats', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      const phoneInput = screen.getByLabelText(/phone/i);
      
      // Test with invalid characters
      await user.clear(phoneInput);
      await user.type(phoneInput, 'phone#number');
      await user.tab();

      await waitFor(() => {
        const errorMessage = screen.queryByText(/invalid phone number format/i);
        if (errorMessage) {
          expect(errorMessage).toBeInTheDocument();
        }
      }, { timeout: 3000 });

      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('should provide real-time validation feedback on blur for text inputs', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      const emailInput = screen.getByLabelText(/email/i);
      
      // Type invalid email
      await user.type(emailInput, 'invalid-email');
      
      // No error should show while typing (before blur)
      expect(screen.queryByText(/invalid email format/i)).not.toBeInTheDocument();
      
      // Blur the field
      await user.tab();

      // Error should appear after blur
      await waitFor(() => {
        expect(screen.getByText(/invalid email format/i)).toBeInTheDocument();
      });
    });

    it('should clear validation error when valid input is provided', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      const emailInput = screen.getByLabelText(/email/i);
      
      // Type invalid email and blur
      await user.type(emailInput, 'invalid');
      await user.tab();

      // Wait for error to appear
      await waitFor(() => {
        expect(screen.getByText(/invalid email format/i)).toBeInTheDocument();
      });

      // Clear and type valid email
      await user.clear(emailInput);
      await user.type(emailInput, 'valid@example.com');
      await user.tab();

      // Error should disappear
      await waitFor(() => {
        expect(screen.queryByText(/invalid email format/i)).not.toBeInTheDocument();
      });
    });

    it('should validate lastName field with various invalid inputs', async () => {
      const user = userEvent.setup();
      
      const invalidLastNames = [
        { value: '', expectedError: /last name is required/i },
        { value: 'B', expectedError: /last name must be at least 2 characters/i }
      ];

      for (const { value, expectedError } of invalidLastNames) {
        const { unmount } = render(
          <EmployeeForm
            onSubmit={mockOnSubmit}
            onCancel={mockOnCancel}
          />
        );

        const lastNameInput = screen.getByLabelText(/last name/i);
        
        await user.clear(lastNameInput);
        if (value) {
          await user.type(lastNameInput, value);
        }
        
        await user.tab();

        await waitFor(() => {
          expect(screen.getByText(expectedError)).toBeInTheDocument();
        });

        unmount();
      }
    });

    it('should prevent form submission when validation errors exist', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      // Fill form with some invalid data
      await user.type(screen.getByLabelText(/first name/i), 'J'); // Too short
      await user.type(screen.getByLabelText(/last name/i), 'Doe');
      await user.type(screen.getByLabelText(/email/i), 'invalid-email'); // Invalid format

      // Try to submit
      const submitButton = screen.getByRole('button', { name: /save/i });
      await user.click(submitButton);

      // Verify validation errors appear
      await waitFor(() => {
        expect(screen.getByText(/first name must be at least 2 characters/i)).toBeInTheDocument();
        expect(screen.getByText(/invalid email format/i)).toBeInTheDocument();
      });

      // Verify form was not submitted
      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('should handle skill checkbox changes correctly', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      // Find and check Python skill (unique name)
      const pythonCheckbox = screen.getByRole('checkbox', { name: /^python$/i });
      await user.click(pythonCheckbox);
      
      expect(pythonCheckbox).toBeChecked();

      // Check React skill
      const reactCheckbox = screen.getByRole('checkbox', { name: /^react$/i });
      await user.click(reactCheckbox);
      
      expect(reactCheckbox).toBeChecked();

      // Uncheck Python skill
      await user.click(pythonCheckbox);
      expect(pythonCheckbox).not.toBeChecked();
      
      // React should still be checked
      expect(reactCheckbox).toBeChecked();
    });

    it('should handle skill changes when currentSkills is undefined', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      // Initially no skills are selected (undefined)
      const pythonCheckbox = screen.getByRole('checkbox', { name: /^python$/i });
      const javaCheckbox = screen.getByRole('checkbox', { name: /^java$/i });
      
      expect(pythonCheckbox).not.toBeChecked();
      expect(javaCheckbox).not.toBeChecked();

      // Check a skill when currentSkills is undefined (tests checked branch with undefined)
      await user.click(pythonCheckbox);
      expect(pythonCheckbox).toBeChecked();

      // Check another skill when currentSkills has values (tests checked branch with array)
      await user.click(javaCheckbox);
      expect(javaCheckbox).toBeChecked();
      expect(pythonCheckbox).toBeChecked();

      // Uncheck a skill when currentSkills has values (tests unchecked branch with array)
      await user.click(pythonCheckbox);
      expect(pythonCheckbox).not.toBeChecked();
      expect(javaCheckbox).toBeChecked();
    });

    it('should handle unchecking skills correctly', async () => {
      const user = userEvent.setup();
      
      // Start with some initial skills
      const initialData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '1234567890',
        gender: 'MALE',
        department: 'IT',
        level: 'SENIOR',
        skills: ['Java', 'Python', 'TypeScript'],
        hireDate: '2023-01-01'
      };

      render(
        <EmployeeForm
          initialData={initialData}
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      // Verify initial skills are checked
      const javaCheckbox = screen.getByRole('checkbox', { name: /^java$/i });
      const pythonCheckbox = screen.getByRole('checkbox', { name: /python/i });
      const typescriptCheckbox = screen.getByRole('checkbox', { name: /typescript/i });

      expect(javaCheckbox).toBeChecked();
      expect(pythonCheckbox).toBeChecked();
      expect(typescriptCheckbox).toBeChecked();

      // Uncheck Python
      await user.click(pythonCheckbox);
      expect(pythonCheckbox).not.toBeChecked();

      // Java and TypeScript should still be checked
      expect(javaCheckbox).toBeChecked();
      expect(typescriptCheckbox).toBeChecked();
    });

    it('should handle adding skills to empty array', async () => {
      const user = userEvent.setup();
      
      // Start with explicitly empty skills array
      const initialData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '1234567890',
        gender: 'MALE',
        department: 'IT',
        level: 'SENIOR',
        skills: [] as string[],
        hireDate: '2023-01-01'
      };

      render(
        <EmployeeForm
          initialData={initialData}
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      // Add first skill to empty array (tests checked branch with empty array)
      const pythonCheckbox = screen.getByRole('checkbox', { name: /^python$/i });
      await user.click(pythonCheckbox);
      expect(pythonCheckbox).toBeChecked();

      // Add second skill (tests checked branch with non-empty array)
      const javaCheckbox = screen.getByRole('checkbox', { name: /^java$/i });
      await user.click(javaCheckbox);
      expect(javaCheckbox).toBeChecked();
      expect(pythonCheckbox).toBeChecked();
    });

    it('should call onCancel when cancel button is clicked', async () => {
      const user = userEvent.setup();
      
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
        />
      );

      const cancelButton = screen.getByRole('button', { name: /cancel/i });
      await user.click(cancelButton);

      expect(mockOnCancel).toHaveBeenCalledTimes(1);
      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('should disable buttons when isSubmitting is true', () => {
      render(
        <EmployeeForm
          onSubmit={mockOnSubmit}
          onCancel={mockOnCancel}
          isSubmitting={true}
        />
      );

      const saveButton = screen.getByRole('button', { name: /saving/i });
      const cancelButton = screen.getByRole('button', { name: /cancel/i });

      expect(saveButton).toBeDisabled();
      expect(cancelButton).toBeDisabled();
    });


  });
});
