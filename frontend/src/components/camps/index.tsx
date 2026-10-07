import { format } from 'date-fns';
import { Camp } from '../../types';

interface CampCardProps {
  camp: Camp;
  onClick?: () => void;
  showOrganizer?: boolean;
}

export function CampCard({ camp, onClick, showOrganizer = false }: CampCardProps) {
  const startDate = new Date(camp.startDate);
  const endDate = new Date(camp.endDate);
  const isUpcoming = startDate > new Date();

  const typeColors: Record<string, string> = {
    DAY: 'badge-primary',
    OVERNIGHT: 'badge-success',
    VIRTUAL: 'badge-secondary',
  };

  return (
    <article className="card h-100" style={{ cursor: onClick ? 'pointer' : 'default' }} onClick={onClick}>
      <div className="card-body d-flex flex-column">
        <div className="d-flex align-items-start justify-content-between mb-2">
          <span className={`badge ${typeColors[camp.type] || 'badge-secondary'}`}>{camp.type}</span>
          {isUpcoming && <span className="badge badge-success ms-2">Upcoming</span>}
        </div>
        <h5 className="card-title mb-2">{camp.title}</h5>
        <p className="card-text text-muted small flex-grow-1">{camp.description.substring(0, 120)}...</p>
        <div className="d-flex align-items-center gap-2 text-muted small mb-2">
          <span><i className="bi bi-calendar3 me-1"></i>{format(startDate, 'MMM d, yyyy')} - {format(endDate, 'MMM d, yyyy')}</span>
          <span className="ms-2"><i className="bi bi-geo-alt me-1"></i>{camp.location}</span>
        </div>
        <div className="d-flex align-items-center justify-content-between text-muted small">
          <span><i className="bi bi-people me-1"></i>{camp.availableSpots} / {camp.capacity} spots</span>
          <span className="ms-2"><i className="bi bi-cash-coin me-1"></i>${camp.price.toFixed(2)}</span>
        </div>
        {showOrganizer && camp.organizer && (
          <div className="mt-2 pt-2 border-top text-muted small">
            <i className="bi bi-person me-1"></i>{camp.organizer.name}
          </div>
        )}
      </div>
    </article>
  );
}

export function CampList({ camps, onCampClick, emptyMessage = 'No camps found' }: { camps: Camp[]; onCampClick?: (camp: Camp) => void; emptyMessage?: string }) {
  if (camps.length === 0) {
    return (
      <div className="text-center py-5">
        <p className="text-muted">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="row g-3">
      {camps.map((camp) => (
        <div key={camp.id} className="col-12 col-md-6 col-lg-4">
          <CampCard camp={camp} onClick={() => onCampClick?.(camp)} />
        </div>
      ))}
    </div>
  );
}

export function CampDetail({ camp }: { camp: Camp }) {
  const startDate = new Date(camp.startDate);
  const endDate = new Date(camp.endDate);

  const typeColors: Record<string, string> = {
    DAY: 'badge-primary',
    OVERNIGHT: 'badge-success',
    VIRTUAL: 'badge-secondary',
  };

  return (
    <div className="card">
      <div className="card-header d-flex align-items-center justify-content-between">
        <h4 className="mb-0">{camp.title}</h4>
        <span className={`badge ${typeColors[camp.type] || 'badge-secondary'}`}>{camp.type}</span>
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
            <p className="mb-0">{camp.location}</p>
          </div>
        </div>

        <div className="row mb-4">
          <div className="col-md-4">
            <h6 className="text-muted mb-2">Age Group</h6>
            <p className="mb-0">{camp.ageGroup}</p>
          </div>
          <div className="col-md-4">
            <h6 className="text-muted mb-2">Capacity</h6>
            <p className="mb-0">{camp.availableSpots} of {camp.capacity} spots</p>
          </div>
          <div className="col-md-4">
            <h6 className="text-muted mb-2">Price</h6>
            <p className="mb-0">${camp.price.toFixed(2)}</p>
          </div>
        </div>

        <div className="row mb-4">
          <div className="col-md-6">
            <h6 className="text-muted mb-2">Status</h6>
            <span className={camp.published ? 'badge badge-success' : 'badge badge-secondary'}>
              {camp.published ? 'Published' : 'Draft'}
            </span>
          </div>
        </div>

        <hr />

        <h6 className="text-muted mb-2">Description</h6>
        <p className="mb-0">{camp.description}</p>

        {camp.organizer && (
          <div className="mt-4 pt-3 border-top">
            <h6 className="text-muted mb-2">Organizer</h6>
            <p className="mb-0"><strong>{camp.organizer.name}</strong> ({camp.organizer.email})</p>
          </div>
        )}
      </div>
    </div>
  );
}