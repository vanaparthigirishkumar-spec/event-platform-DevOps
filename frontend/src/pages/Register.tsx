import { RegisterForm } from '../components/auth/RegisterForm';
import { Link } from 'react-router-dom';

export function Register() {
  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center py-5 px-3">
      <div className="w-100" style={{ maxWidth: '450px' }}>
        <div className="text-center mb-4">
          <Link to="/" className="text-primary fw-bold fs-3 text-decoration-none">EventPlatform</Link>
          <h2 className="mt-3 mb-1">Create Account</h2>
          <p className="text-muted">Join EventPlatform today</p>
        </div>
        <RegisterForm />
      </div>
    </div>
  );
}