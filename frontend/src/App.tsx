import { Routes, Route, Navigate } from 'react-router-dom';
import { Header, Footer, ProtectedRoute } from './components/layout';
import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Events } from './pages/Events';
import { EventDetail } from './pages/EventDetail';
import { CreateEvent } from './pages/CreateEvent';
import { Camps } from './pages/Camps';
import { CampDetail } from './pages/CampDetail';
import { CreateCamp } from './pages/CreateCamp';
import { Dashboard } from './pages/Dashboard';
import { Profile } from './pages/Profile';
import { useAuth } from './contexts/AuthContext';

function PublicRoutes() {
  return (
    <>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/events" element={<Events />} />
      <Route path="/events/:id" element={<EventDetail />} />
      <Route path="/camps" element={<Camps />} />
      <Route path="/camps/:id" element={<CampDetail />} />
    </>
  );
}

function PrivateRoutes() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="d-flex align-items-center justify-content-center vh-100">
        <span className="spinner" style={{ width: '3rem', height: '3rem', borderWidth: '3px' }} />
      </div>
    );
  }

  return (
    <>
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/create-event"
        element={
          <ProtectedRoute>
            <CreateEvent />
          </ProtectedRoute>
        }
      />
      <Route
        path="/create-camp"
        element={
          <ProtectedRoute>
            <CreateCamp />
          </ProtectedRoute>
        }
      />
      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <Profile />
          </ProtectedRoute>
        }
      />
    </>
  );
}

function App() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="d-flex flex-column min-vh-100">
      <Header />
      <main className="flex-grow-1 container py-4">
        <Routes>
          <PublicRoutes />
          <PrivateRoutes />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;