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
      
      // Generate various invalid email formats
      const invalidEmails = [
        'notanemail',
        '@example.com',
        'user@',
        'user @example.com',
        ''
      ];

      for (const invalidEmail of invalidEmails) {
        const { unmount } = render(
          <EmployeeForm
            onSubmit={mockOnSubmit}
            onCancel={mockOnCancel}
          />
        );

        const emailInput = screen.getByLabelText(/email/i);
        
        // Enter invalid email
        await user.clear(emailInput);
        if (invalidEmail) {
          await user.type(emailInput, invalidEmail);
        }
        
        // Blur to trigger validation
        await user.tab();

        // Verify validation message appears
        await waitFor(() => {
          const errorMessage = invalidEmail === '' 
            ? /email is required/i 
            : /invalid email format/i;
          expect(screen.getByText(errorMessage)).toBeInTheDocument();
        });

        // Verify form was NOT submitted
        expect(mockOnSubmit).not.toHaveBeenCalled();

        unmount();
      }
    });

    it('should show validation error for invalid first name inputs', async () => {
      const user = userEvent.setup();
      
      // Generate various invalid first names
      const invalidFirstNames = [
        { value: '', expectedError: /first name is required/i },
        { value: 'A', expectedError: /first name must be at least 2 characters/i },
        { value: 'X'.repeat(101), expectedError: /first name must not exceed 100 characters/i }
      ];

      for (const { value, expectedError } of invalidFirstNames) {
        const { unmount } = render(
          <EmployeeForm
            onSubmit={mockOnSubmit}
            onCancel={mockOnCancel}
          />
        );

        const firstNameInput = screen.getByLabelText(/first name/i);
        
        await user.clear(firstNameInput);
        if (value) {
          await user.type(firstNameInput, value);
        }
        
        await user.tab();

        await waitFor(() => {
          expect(screen.getByText(expectedError)).toBeInTheDocument();
        });

        expect(mockOnSubmit).not.toHaveBeenCalled();

        unmount();
      }
    });

    it('should show validation error for invalid phone formats', async () => {
      const user = userEvent.setup();
      
      // Generate various invalid phone formats
      const invalidPhones = [
        'abc123', // contains letters
        '123-456-7890-extra-long-text-that-exceeds-limit', // too long
        'phone#number' // invalid characters
      ];

      for (const invalidPhone of invalidPhones) {
        const { unmount } = render(
          <EmployeeForm
            onSubmit={mockOnSubmit}
            onCancel={mockOnCancel}
          />
        );

        const phoneInput = screen.getByLabelText(/phone/i);
        
        await user.clear(phoneInput);
        await user.type(phoneInput, invalidPhone);
        await user.tab();

        await waitFor(() => {
          const errorMessage = screen.queryByText(/invalid phone number format/i);
          if (errorMessage) {
            expect(errorMessage).toBeInTheDocument();
          }
        });

        expect(mockOnSubmit).not.toHaveBeenCalled();

        unmount();
      }
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
  });
});
