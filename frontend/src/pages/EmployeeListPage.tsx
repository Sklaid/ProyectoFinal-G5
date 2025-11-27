import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Button,
  Box,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { EmployeeTable } from '../components/EmployeeTable';
import { employeeService } from '../services/employeeService';
import { Employee } from '../types/employee';

export const EmployeeListPage: React.FC = () => {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [employeeToDelete, setEmployeeToDelete] = useState<number | null>(null);

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await employeeService.getAll();
      setEmployees(data);
    } catch (err) {
      setError('Failed to load employees. Please try again.');
      console.error('Error fetching employees:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateNew = () => {
    navigate('/employees/new');
  };

  const handleEdit = (employee: Employee) => {
    navigate(`/employees/${employee.id}/edit`);
  };

  const handleDeleteClick = (id: number) => {
    setEmployeeToDelete(id);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (employeeToDelete === null) return;

    try {
      await employeeService.delete(employeeToDelete);
      setEmployees(employees.filter(emp => emp.id !== employeeToDelete));
      setDeleteDialogOpen(false);
      setEmployeeToDelete(null);
    } catch (err) {
      setError('Failed to delete employee. Please try again.');
      console.error('Error deleting employee:', err);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setEmployeeToDelete(null);
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
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Employees
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={handleCreateNew}
        >
          Create New
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <EmployeeTable
        employees={employees}
        onEdit={handleEdit}
        onDelete={handleDeleteClick}
      />

      <Dialog
        open={deleteDialogOpen}
        onClose={handleDeleteCancel}
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this employee? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel}>Cancel</Button>
          <Button onClick={handleDeleteConfirm} color="error" autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};
