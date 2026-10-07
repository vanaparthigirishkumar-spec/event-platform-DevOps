import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useEvent } from '../hooks/useApi';
import { EventDetail as EventDetailComponent } from '../components/events/index';
import { Button, Card, Spinner } from '../components/common/index';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { api } from '../services/api';

export function EventDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const [isDeleting, setIsDeleting] = useState(false);

  const { data: event, isLoading, error } = useEvent(id);

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this event? This action cannot be undone.')) {
      return;
    }

    setIsDeleting(true);
    try {
      await api.deleteEvent(id!);
      showToast({ type: 'success', message: 'Event deleted successfully' });
      navigate('/events');
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to delete event';
      showToast({ type: 'error', message });
    } finally {
      setIsDeleting(false);
    }
  };

  const canModify = isAuthenticated && event && (event.organizer.id === user?.id || user?.role === 'ADMIN');

  if (isLoading) {
    return (
      <div className="d-flex align-items-center justify-content-center py-5">
        <Spinner size="lg" />
      </div>
    );
  }

  if (error || !event) {
    return (
      <div className="text-center py-5">
        <h3>Event not found</h3>
        <p className="text-muted">{error?.message || 'The event you\'re looking for doesn\'t exist.'}</p>
        <Link to="/events" className="btn btn-primary mt-3">Back to Events</Link>
      </div>
    );
  }

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <div>
          <Link to="/events" className="btn btn-outline btn-sm mb-2">&larr; Back to Events</Link>
          <h2 className="mb-0">{event.title}</h2>
        </div>
        {canModify && (
          <div className="d-flex gap-2">
            <Link to={`/events/${event.id}/edit`} className="btn btn-outline">Edit</Link>
            <Button variant="danger" onClick={handleDelete} isLoading={isDeleting}>
              Delete
            </Button>
          </div>
        )}
      </div>

      <EventDetailComponent event={event} />
    </div>
  );
}