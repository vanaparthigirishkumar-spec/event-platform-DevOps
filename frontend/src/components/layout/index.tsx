import { useState } from 'react';
import { Link, useLocation, NavLink } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { Button } from '../common';

export function Header() {
  const { user, isAuthenticated, logout } = useAuth();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navLinks = [
    { path: '/events', label: 'Events' },
    { path: '/camps', label: 'Camps' },
  ];

  const authLinks = isAuthenticated
    ? [
        { path: '/dashboard', label: 'Dashboard' },
        { path: '/create-event', label: 'Create Event' },
        { path: '/create-camp', label: 'Create Camp' },
        { path: '/profile', label: 'Profile' },
      ]
    : [
        { path: '/login', label: 'Login' },
        { path: '/register', label: 'Register' },
      ];

  return (
    <header className="bg-white shadow-sm sticky-top">
      <nav className="container d-flex align-items-center justify-content-between py-2" aria-label="Main navigation">
        <Link to="/" className="text-primary fw-bold fs-4 text-decoration-none" aria-label="Event Platform Home">
          EventPlatform
        </Link>

        <div className={`d-none d-md-flex align-items-center gap-3 ${mobileMenuOpen ? 'show' : ''}`} style={{ position: 'absolute', top: '100%', left: 0, right: 0, background: 'white', padding: '1rem', boxShadow: '0 0.5rem 1rem rgba(0,0,0,0.1)', zIndex: 1000 }}>
          <div className="d-flex flex-column flex-md-row align-items-center gap-2 w-100">
            {navLinks.map((link) => (
              <NavLink
                key={link.path}
                to={link.path}
                className={({ isActive }) => `text-decoration-none ${isActive ? 'text-primary fw-medium' : 'text-dark'}`}
                end
              >
                {link.label}
              </NavLink>
            ))}
            {authLinks.map((link) => (
              <NavLink
                key={link.path}
                to={link.path}
                className={({ isActive }) => `btn ${isActive ? 'btn-primary' : 'btn-outline'} ${link.path === '/login' || link.path === '/register' ? 'btn-outline' : ''}`}
              >
                {link.label}
              </NavLink>
            ))}
            {isAuthenticated && (
              <Button variant="danger" size="sm" onClick={logout}>
                Logout
              </Button>
            )}
          </div>
        </div>

        <button
          className="btn btn-outline d-md-none"
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-expanded={mobileMenuOpen}
          aria-controls="mobile-menu"
          aria-label="Toggle navigation menu"
        >
          <span className="spinner" style={{ width: '1.5rem', height: '1.5rem', borderWidth: '2px' }} />
        </button>
      </nav>
    </header>
  );
}

export function Footer() {
  return (
    <footer className="bg-light border-top mt-auto py-4">
      <div className="container text-center text-muted">
        <p className="mb-0">&copy; 2025 EventPlatform. All rights reserved.</p>
      </div>
    </footer>
  );
}

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="d-flex align-items-center justify-content-center vh-100">
        <span className="spinner" style={{ width: '3rem', height: '3rem', borderWidth: '3px' }} />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

import { Navigate } from 'react-router-dom';