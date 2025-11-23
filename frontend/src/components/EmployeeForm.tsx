import React from 'react';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  TextField,
  Button,
  Box,
  Grid,
  FormControl,
  FormLabel,
  RadioGroup,
  FormControlLabel,
  Radio,
  FormGroup,
  Checkbox,
  Select,
  MenuItem,
  InputLabel,
  FormHelperText,
  Paper
} from '@mui/material';
import { Gender, Department, Level, EmployeeFormData } from '../types/employee';

const AVAILABLE_SKILLS = [
  'Java',
  'Python',
  'JavaScript',
  'TypeScript',
  'React',
  'Angular',
  'Vue',
  'Spring Boot',
  'Node.js',
  'Docker',
  'Kubernetes',
  'AWS',
  'Azure'
];

const validationSchema = yup.object({
  firstName: yup
    .string()
    .required('First name is required')
    .min(2, 'First name must be at least 2 characters')
    .max(100, 'First name must not exceed 100 characters'),
  lastName: yup
    .string()
    .required('Last name is required')
    .min(2, 'Last name must be at least 2 characters')
    .max(100, 'Last name must not exceed 100 characters'),
  email: yup
    .string()
    .required('Email is required')
    .email('Invalid email format'),
  phone: yup
    .string()
    .matches(/^[0-9+\-() ]*$/, 'Invalid phone number format')
    .max(20, 'Phone number must not exceed 20 characters'),
  gender: yup
    .string()
    .required('Gender is required')
    .oneOf(Object.values(Gender), 'Invalid gender'),
  department: yup
    .string()
    .required('Department is required')
    .oneOf(Object.values(Department), 'Invalid department'),
  level: yup
    .string()
    .required('Level is required')
    .oneOf(Object.values(Level), 'Invalid level'),
  skills: yup
    .array()
    .of(yup.string())
    .min(1, 'At least one skill is required'),
  hireDate: yup
    .string()
    .required('Hire date is required')
});

interface EmployeeFormProps {
  initialData?: EmployeeFormData;
  onSubmit: (data: EmployeeFormData) => void;
  onCancel: () => void;
  isSubmitting?: boolean;
}

export const EmployeeForm: React.FC<EmployeeFormProps> = ({
  initialData,
  onSubmit,
  onCancel,
  isSubmitting = false
}) => {
  const {
    control,
    handleSubmit,
    formState: { errors }
  } = useForm<EmployeeFormData>({
    resolver: yupResolver(validationSchema),
    mode: 'onBlur', // Validate on blur to provide real-time feedback
    defaultValues: initialData || {
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      gender: '',
      department: '',
      level: '',
      skills: [],
      hireDate: ''
    }
  });

  return (
    <Paper sx={{ p: 3 }}>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Grid container spacing={3}>
          {/* First Name */}
          <Grid item xs={12} sm={6}>
            <Controller
              name="firstName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="First Name"
                  fullWidth
                  error={!!errors.firstName}
                  helperText={errors.firstName?.message}
                  required
                />
              )}
            />
          </Grid>

          {/* Last Name */}
          <Grid item xs={12} sm={6}>
            <Controller
              name="lastName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Last Name"
                  fullWidth
                  error={!!errors.lastName}
                  helperText={errors.lastName?.message}
                  required
                />
              )}
            />
          </Grid>

          {/* Email */}
          <Grid item xs={12} sm={6}>
            <Controller
              name="email"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Email"
                  type="email"
                  fullWidth
                  error={!!errors.email}
                  helperText={errors.email?.message}
                  required
                />
              )}
            />
          </Grid>

          {/* Phone */}
          <Grid item xs={12} sm={6}>
            <Controller
              name="phone"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Phone"
                  fullWidth
                  error={!!errors.phone}
                  helperText={errors.phone?.message}
                />
              )}
            />
          </Grid>

          {/* Gender - Radio Buttons */}
          <Grid item xs={12}>
            <FormControl error={!!errors.gender} required>
              <FormLabel>Gender</FormLabel>
              <Controller
                name="gender"
                control={control}
                render={({ field }) => (
                  <RadioGroup {...field} row>
                    <FormControlLabel
                      value={Gender.MALE}
                      control={<Radio />}
                      label="Male"
                    />
                    <FormControlLabel
                      value={Gender.FEMALE}
                      control={<Radio />}
                      label="Female"
                    />
                    <FormControlLabel
                      value={Gender.OTHER}
                      control={<Radio />}
                      label="Other"
                    />
                  </RadioGroup>
                )}
              />
              {errors.gender && (
                <FormHelperText>{errors.gender.message}</FormHelperText>
              )}
            </FormControl>
          </Grid>

          {/* Department - Combobox */}
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth error={!!errors.department} required>
              <InputLabel>Department</InputLabel>
              <Controller
                name="department"
                control={control}
                render={({ field }) => (
                  <Select {...field} label="Department">
                    <MenuItem value={Department.IT}>IT</MenuItem>
                    <MenuItem value={Department.HR}>HR</MenuItem>
                    <MenuItem value={Department.FINANCE}>Finance</MenuItem>
                    <MenuItem value={Department.SALES}>Sales</MenuItem>
                  </Select>
                )}
              />
              {errors.department && (
                <FormHelperText>{errors.department.message}</FormHelperText>
              )}
            </FormControl>
          </Grid>

          {/* Level - Combobox */}
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth error={!!errors.level} required>
              <InputLabel>Level</InputLabel>
              <Controller
                name="level"
                control={control}
                render={({ field }) => (
                  <Select {...field} label="Level">
                    <MenuItem value={Level.JUNIOR}>Junior</MenuItem>
                    <MenuItem value={Level.MID}>Mid</MenuItem>
                    <MenuItem value={Level.SENIOR}>Senior</MenuItem>
                    <MenuItem value={Level.LEAD}>Lead</MenuItem>
                  </Select>
                )}
              />
              {errors.level && (
                <FormHelperText>{errors.level.message}</FormHelperText>
              )}
            </FormControl>
          </Grid>

          {/* Skills - Checkboxes */}
          <Grid item xs={12}>
            <FormControl error={!!errors.skills} required>
              <FormLabel>Skills (select at least one)</FormLabel>
              <Controller
                name="skills"
                control={control}
                render={({ field }) => (
                  <FormGroup row>
                    {AVAILABLE_SKILLS.map((skill) => (
                      <FormControlLabel
                        key={skill}
                        control={
                          <Checkbox
                            checked={field.value?.includes(skill) || false}
                            onChange={(e) => {
                              const newSkills = e.target.checked
                                ? [...(field.value || []), skill]
                                : (field.value || []).filter((s) => s !== skill);
                              field.onChange(newSkills);
                            }}
                          />
                        }
                        label={skill}
                      />
                    ))}
                  </FormGroup>
                )}
              />
              {errors.skills && (
                <FormHelperText>{errors.skills.message}</FormHelperText>
              )}
            </FormControl>
          </Grid>

          {/* Hire Date - Date Picker */}
          <Grid item xs={12} sm={6}>
            <Controller
              name="hireDate"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Hire Date"
                  type="date"
                  fullWidth
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.hireDate}
                  helperText={errors.hireDate?.message}
                  required
                />
              )}
            />
          </Grid>

          {/* Action Buttons */}
          <Grid item xs={12}>
            <Box display="flex" gap={2} justifyContent="flex-end">
              <Button
                variant="outlined"
                onClick={onCancel}
                disabled={isSubmitting}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Saving...' : 'Save'}
              </Button>
            </Box>
          </Grid>
        </Grid>
      </form>
    </Paper>
  );
};
