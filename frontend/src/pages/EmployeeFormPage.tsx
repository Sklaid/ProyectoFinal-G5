import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  CircularProgress,
  Alert
} from '@mui/material';
import { EmployeeForm } from '../components/EmployeeForm';
import { employeeService } from '../services/employeeService';
import { EmployeeFormData } from '../types/employee';

export const EmployeeFormPage: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const isEditMode = !!id;

  const [initialData, setInitialData] = useState<EmployeeFormData | undefined>();
  const [loading, setLoading] = useState(isEditMode);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (isEditMode && id) {
      fetchEmployee(parseInt(id, 10));
    }
  }, [id, isEditMode]);

  const fetchEmployee = async (employeeId: number) => {
    try {
      setLoading(true);
      setError(null);
      const employee = await employeeService.getById(employeeId);
      
      // Convert Employee to EmployeeFormData
      setInitialData({
        firstName: employee.firstName,
        lastName: employee.lastName,
        email: employee.email,
        phone: employee.phone || '',
        gender: employee.gender,
        department: employee.department,
        level: employee.level,
        skills: employee.skills,
        hireDate: employee.hireDate
      });
    } catch (err) {
      setError('Failed to load employee data. Please try again.');
      console.error('Error fetching employee:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (data: EmployeeFormData) => {
    try {
      setIsSubmitting(true);
      setError(null);

      if (isEditMode && id) {
        await employeeService.update(parseInt(id, 10), data);
      } else {
        await employeeService.create(data);
      }

      navigate('/employees');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 
        `Failed to ${isEditMode ? 'update' : 'create'} employee. Please try again.`;
      setError(errorMessage);
      console.error('Error saving employee:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate('/employees');
  };

  if (loading) {
    return (
      <Container>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {isEditMode ? 'Edit Employee' : 'Create New Employee'}
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <EmployeeForm
        initialData={initialData}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isSubmitting={isSubmitting}
      />
    </Container>
  );
};
