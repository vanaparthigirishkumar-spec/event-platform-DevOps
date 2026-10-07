import { format } from 'date-fns';
import { Event } from '../../types';

interface EventCardProps {
  event: Event;
  onClick?: () => void;
  showOrganizer?: boolean;
}

export function EventCard({ event, onClick, showOrganizer = false }: EventCardProps) {
  const startDate = new Date(event.startDate);
  const endDate = new Date(event.endDate);
  const isUpcoming = startDate > new Date();

  return (
    <article className="card h-100" style={{ cursor: onClick ? 'pointer' : 'default' }} onClick={onClick}>
      <div className="card-body d-flex flex-column">
        <div className="d-flex align-items-start justify-content-between mb-2">
          <span className="badge badge-primary">{event.category}</span>
          {isUpcoming && <span className="badge badge-success ms-2">Upcoming</span>}
        </div>
        <h5 className="card-title mb-2">{event.title}</h5>
        <p className="card-text text-muted small flex-grow-1">{event.description.substring(0, 120)}...</p>
        <div className="d-flex align-items-center gap-2 text-muted small mb-2">
          <span><i className="bi bi-calendar3 me-1"></i>{format(startDate, 'MMM d, yyyy')} - {format(endDate, 'MMM d, yyyy')}</span>
          <span className="ms-2"><i className="bi bi-geo-alt me-1"></i>{event.location}</span>
        </div>
        <div className="d-flex align-items-center justify-content-between text-muted small">
          <span><i className="bi bi-people me-1"></i>{event.availableSpots} / {event.capacity} spots</span>
          {showOrganizer && event.organizer && (
            <span><i className="bi bi-person me-1"></i>{event.organizer.name}</span>
          )}
        </div>
      </div>
    </article>
  );
}

export function EventList({ events, onEventClick, emptyMessage = 'No events found' }: { events: Event[]; onEventClick?: (event: Event) => void; emptyMessage?: string }) {
  if (events.length === 0) {
    return (
      <div className="text-center py-5">
        <p className="text-muted">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="row g-3">
      {events.map((event) => (
        <div key={event.id} className="col-12 col-md-6 col-lg-4">
          <EventCard event={event} onClick={() => onEventClick?.(event)} />
        </div>
      ))}
    </div>
  );
}

export function EventDetail({ event }: { event: Event }) {
  const startDate = new Date(event.startDate);
  const endDate = new Date(event.endDate);

  return (
    <div className="card">
      <div className="card-header d-flex align-items-center justify-content-between">
        <h4 className="mb-0">{event.title}</h4>
        <span className="badge badge-primary">{event.category}</span>
      </div>
      <div className="card-body">
        <div className="row mb-4">
          <div className="col-md-6">
            <h6 className="text-muted mb-2">Date & Time</h6>
            <p className="mb-1"><strong>Start:</strong> {format(startDate, 'EEEE, MMMM d, yyyy \'at\' h:mm a')}</p>
            <p className="mb-0"><strong>End:</strong> {format(endDate, 'EEEE, MMMM d, yyyy \'at\' h:mm a')}</p>
          </div>
          <div className="col-md-6">
            <h6 className="text-muted mb-2">Location</h6>
            <p className="mb-0">{event.location}</p>
          </div>
        </div>

        <div className="row mb-4">
          <div className="col-md-6">
            <h6 className="text-muted mb-2">Capacity</h6>
            <p className="mb-0">{event.availableSpots} of {event.capacity} spots available</p>
          </div>
          <div className="col-md-6">
            <h6 className="text-muted mb-2">Status</h6>
            <span className={event.published ? 'badge badge-success' : 'badge badge-secondary'}>
              {event.published ? 'Published' : 'Draft'}
            </span>
          </div>
        </div>

        <hr />

        <h6 className="text-muted mb-2">Description</h6>
        <p className="mb-0">{event.description}</p>

        {event.organizer && (
          <div className="mt-4 pt-3 border-top">
            <h6 className="text-muted mb-2">Organizer</h6>
            <p className="mb-0"><strong>{event.organizer.name}</strong> ({event.organizer.email})</p>
          </div>
        )}
      </div>
    </div>
  );
}