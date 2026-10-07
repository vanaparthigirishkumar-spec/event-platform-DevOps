import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useToast } from '../../contexts/ToastContext';
import { Button, Input, Card } from '../common';

const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(1, 'Password is required'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export function LoginForm() {
  const { login } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const {
    register: registerField,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data.email, data.password);
      showToast({ type: 'success', message: 'Welcome back!' });
      navigate('/dashboard');
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Login failed. Please check your credentials.';
      showToast({ type: 'error', message });
    }
  };

  return (
    <Card className="w-100" style={{ maxWidth: '450px' }}>
      <div className="card-header text-center">
        <h3 className="mb-0">Welcome Back</h3>
      </div>
      <form onSubmit={handleSubmit(onSubmit)} className="p-3" noValidate>
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

        <Button type="submit" className="w-100" size="lg" isLoading={isSubmitting}>
          Login
        </Button>
      </form>
      <div className="card-footer text-center py-3">
        <p className="mb-0 text-muted">
          Don't have an account? <Link to="/register" className="text-primary fw-medium">Register</Link>
        </p>
      </div>
    </Card>
  );
}