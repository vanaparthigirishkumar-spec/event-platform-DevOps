import { LoginForm } from '../components/auth/LoginForm';
import { Card } from '../components/common/index';

export function Login() {
  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center py-5 px-3">
      <div className="w-100" style={{ maxWidth: '450px' }}>
        <div className="text-center mb-4">
          <Link to="/" className="text-primary fw-bold fs-3 text-decoration-none">EventPlatform</Link>
          <h2 className="mt-3 mb-1">Welcome Back</h2>
          <p className="text-muted">Sign in to your account</p>
        </div>
        <LoginForm />
      </div>
    </div>
  );
}

import { Link } from 'react-router-dom';