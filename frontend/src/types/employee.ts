// Employee types and interfaces

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER'
}

export enum Department {
  IT = 'IT',
  HR = 'HR',
  FINANCE = 'FINANCE',
  SALES = 'SALES'
}

export enum Level {
  JUNIOR = 'JUNIOR',
  MID = 'MID',
  SENIOR = 'SENIOR',
  LEAD = 'LEAD'
}

export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  gender: Gender;
  department: Department;
  level: Level;
  skills: string[];
  hireDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeFormData {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  gender: string;
  department: string;
  level: string;
  skills: string[];
  hireDate: string;
}
