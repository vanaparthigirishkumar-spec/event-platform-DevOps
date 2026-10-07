import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useToast } from '../../contexts/ToastContext';
import { Button, Input, Card } from '../common';

const registerSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters').max(100),
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});

type RegisterFormData = z.infer<typeof registerSchema>;

export function RegisterForm() {
  const { register } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const {
    register: registerField,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    try {
      await register(data.email, data.password, data.name);
      showToast({ type: 'success', message: 'Registration successful! Welcome to EventPlatform.' });
      navigate('/dashboard');
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Registration failed. Please try again.';
      showToast({ type: 'error', message });
    }
  };

  return (
    <Card className="w-100" style={{ maxWidth: '450px' }}>
      <div className="card-header text-center">
        <h3 className="mb-0">Create Account</h3>
      </div>
      <form onSubmit={handleSubmit(onSubmit)} className="p-3" noValidate>
        <div className="mb-3">
          <label htmlFor="name" className="form-label">Name</label>
          <input
            {...registerField('name')}
            type="text"
            id="name"
            className={`form-control ${errors.name ? 'is-invalid' : ''}`}
            placeholder="Enter your name"
            disabled={isSubmitting}
          />
          {errors.name && <div className="invalid-feedback">{errors.name.message}</div>}
        </div>

        <div className="mb-3">
          <label htmlFor="email" className="form-label">Email</label>
          <input
            {...registerField('email')}
            type="email"
            id="email"
            className={`form-control ${errors.email ? 'is-invalid' : ''}`}
            placeholder="Enter your email"
            disabled={isSubmitting}
          />
          {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
        </div>

        <div className="mb-3">
          <label htmlFor="password" className="form-label">Password</label>
          <input
            {...registerField('password')}
            type="password"
            id="password"
            className={`form-control ${errors.password ? 'is-invalid' : ''}`}
            placeholder="Enter your password"
            disabled={isSubmitting}
          />
          {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
        </div>

        <div className="mb-3">
          <label htmlFor="confirmPassword" className="form-label">Confirm Password</label>
          <input
            {...registerField('confirmPassword')}
            type="password"
            id="confirmPassword"
            className={`form-control ${errors.confirmPassword ? 'is-invalid' : ''}`}
            placeholder="Confirm your password"
            disabled={isSubmitting}
          />
          {errors.confirmPassword && <div className="invalid-feedback">{errors.confirmPassword.message}</div>}
        </div>

        <Button type="submit" className="w-100" size="lg" isLoading={isSubmitting}>
          Create Account
        </Button>
      </form>
      <div className="card-footer text-center py-3">
        <p className="mb-0 text-muted">
          Already have an account? <Link to="/login" className="text-primary fw-medium">Login</Link>
        </p>
      </div>
    </Card>
  );
}