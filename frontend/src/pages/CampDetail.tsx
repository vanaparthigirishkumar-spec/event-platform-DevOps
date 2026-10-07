import { useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useCamp } from '../hooks/useApi';
import { CampDetail as CampDetailComponent } from '../components/camps/index';
import { Button, Card, Spinner } from '../components/common/index';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { api } from '../services/api';

export function CampDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const [isDeleting, setIsDeleting] = useState(false);

  const { data: camp, isLoading, error } = useCamp(id);

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this camp? This action cannot be undone.')) {
      return;
    }

    setIsDeleting(true);
    try {
      await api.deleteCamp(id!);
      showToast({ type: 'success', message: 'Camp deleted successfully' });
      navigate('/camps');
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to delete camp';
      showToast({ type: 'error', message });
    } finally {
      setIsDeleting(false);
    }
  };

  const canModify = isAuthenticated && camp && (camp.organizer.id === user?.id || user?.role === 'ADMIN');

  if (isLoading) {
    return (
      <div className="d-flex align-items-center justify-content-center py-5">
        <Spinner size="lg" />
      </div>
    );
  }

  if (error || !camp) {
    return (
      <div className="text-center py-5">
        <h3>Camp not found</h3>
        <p className="text-muted">{error?.message || 'The camp you\'re looking for doesn\'t exist.'}</p>
        <Link to="/camps" className="btn btn-primary mt-3">Back to Camps</Link>
      </div>
    );
  }

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <div>
          <Link to="/camps" className="btn btn-outline btn-sm mb-2">&larr; Back to Camps</Link>
          <h2 className="mb-0">{camp.title}</h2>
        </div>
        {canModify && (
          <div className="d-flex gap-2">
            <Link to={`/camps/${camp.id}/edit`} className="btn btn-outline">Edit</Link>
            <Button variant="danger" onClick={handleDelete} isLoading={isDeleting}>
              Delete
            </Button>
          </div>
        )}
      </div>

      <CampDetailComponent camp={camp} />
    </div>
  );
}