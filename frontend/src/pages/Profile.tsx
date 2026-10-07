import { useAuth } from '../contexts/AuthContext';
import { Card, Button } from '../components/common/index';
import { Link } from 'react-router-dom';

export function Profile() {
  const { user, refreshUser } = useAuth();

  return (
    <div className="container" style={{ maxWidth: '600px' }}>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <h2 className="mb-0">Profile</h2>
      </div>

      <Card className="mb-4">
        <div className="card-body text-center py-4">
          <div className="d-flex align-items-center justify-content-center gap-3 mb-3">
            <div className="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style={{ width: '80px', height: '80px', fontSize: '2rem' }}>
              {user?.name?.charAt(0).toUpperCase()}
            </div>
            <div className="text-start">
              <h3 className="mb-1">{user?.name}</h3>
              <p className="text-muted mb-0">{user?.email}</p>
            </div>
          </div>
          <div className="d-flex justify-content-center gap-2 mb-3">
            <span className="badge bg-primary fs-6 px-3 py-2">{user?.role}</span>
            <span className="badge bg-secondary fs-6 px-3 py-2">Member since {user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A'}</span>
          </div>
        </div>
      </Card>

      <Card className="mb-4">
        <div className="card-header">
          <h5 className="mb-0">Account Settings</h5>
        </div>
        <div className="card-body">
          <p className="text-muted mb-4">Manage your account preferences and security settings.</p>
          <div className="d-flex gap-2">
            <Link to="/profile/edit" className="btn btn-outline">Edit Profile</Link>
            <Link to="/profile/password" className="btn btn-outline">Change Password</Link>
          </div>
        </div>
      </Card>

      <Card className="mb-4">
        <div className="card-header">
          <h5 className="mb-0">Quick Actions</h5>
        </div>
        <div className="card-body">
          <div className="d-flex flex-wrap gap-2">
            <Link to="/create-event" className="btn btn-primary">Create Event</Link>
            <Link to="/create-camp" className="btn btn-success">Create Camp</Link>
            <Link to="/dashboard/events" className="btn btn-outline">My Events</Link>
            <Link to="/dashboard/camps" className="btn btn-outline">My Camps</Link>
          </div>
        </div>
      </Card>

      <Card className="border-danger">
        <div className="card-header">
          <h5 className="mb-0 text-danger">Danger Zone</h5>
        </div>
        <div className="card-body">
          <p className="text-muted mb-3">Once you delete your account, there is no going back. Please be certain.</p>
          <button className="btn btn-danger">Delete Account</button>
        </div>
      </Card>
    </div>
  );
}