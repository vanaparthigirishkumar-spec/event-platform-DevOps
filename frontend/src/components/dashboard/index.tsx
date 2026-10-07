import { useAuth } from '../../contexts/AuthContext';
import { useMyEvents } from '../../hooks/useApi';
import { useMyCamps } from '../../hooks/useApi';
import { EventCard } from '../events/index';
import { CampCard } from '../camps/index';
import { Card, Spinner } from '../common/index';

export function Dashboard() {
  const { user } = useAuth();
  const { data: eventsData, isLoading: eventsLoading } = useMyEvents({ size: 5 });
  const { data: campsData, isLoading: campsLoading } = useMyCamps({ size: 5 });

  if (eventsLoading || campsLoading) {
    return (
      <div className="d-flex align-items-center justify-content-center py-5">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <div>
          <h2 className="mb-1">Welcome back, {user?.name}!</h2>
          <p className="text-muted mb-0">Manage your events and camps</p>
        </div>
        <div className="d-flex gap-2">
          <a href="/create-event" className="btn btn-primary">Create Event</a>
          <a href="/create-camp" className="btn btn-outline">Create Camp</a>
        </div>
      </div>

      <div className="row g-4 mb-5">
        <div className="col-12 col-md-6 col-lg-3">
          <Card className="text-center">
            <div className="display-4 text-primary fw-bold">{eventsData?.totalElements || 0}</div>
            <div className="text-muted">Total Events</div>
          </Card>
        </div>
        <div className="col-12 col-md-6 col-lg-3">
          <Card className="text-center">
            <div className="display-4 text-success fw-bold">{campsData?.totalElements || 0}</div>
            <div className="text-muted">Total Camps</div>
          </Card>
        </div>
        <div className="col-12 col-md-6 col-lg-3">
          <Card className="text-center">
            <div className="display-4 text-info fw-bold">
              {(eventsData?.content?.reduce((sum, e) => sum + e.availableSpots, 0) || 0) + (campsData?.content?.reduce((sum, c) => sum + c.availableSpots, 0) || 0)}
            </div>
            <div className="text-muted">Available Spots</div>
          </Card>
        </div>
        <div className="col-12 col-md-6 col-lg-3">
          <Card className="text-center">
            <div className="display-4 text-warning fw-bold">{user?.role}</div>
            <div className="text-muted">Your Role</div>
          </Card>
        </div>
      </div>

      <div className="mb-5">
        <div className="d-flex align-items-center justify-content-between mb-3">
          <h3 className="mb-0">Your Events</h3>
          <a href="/dashboard/events" className="btn btn-sm btn-outline">View All</a>
        </div>
        {eventsData?.content && eventsData.content.length > 0 ? (
          <div className="row g-3">
            {eventsData.content.slice(0, 3).map((event) => (
              <div key={event.id} className="col-12 col-md-6">
                <EventCard event={event} showOrganizer />
              </div>
            ))}
          </div>
        ) : (
          <Card className="text-center py-4">
            <p className="text-muted mb-3">You haven't created any events yet.</p>
            <a href="/create-event" className="btn btn-primary">Create Your First Event</a>
          </Card>
        )}
      </div>

      <div>
        <div className="d-flex align-items-center justify-content-between mb-3">
          <h3 className="mb-0">Your Camps</h3>
          <a href="/dashboard/camps" className="btn btn-sm btn-outline">View All</a>
        </div>
        {campsData?.content && campsData.content.length > 0 ? (
          <div className="row g-3">
            {campsData.content.slice(0, 3).map((camp) => (
              <div key={camp.id} className="col-12 col-md-6">
                <CampCard camp={camp} showOrganizer />
              </div>
            ))}
          </div>
        ) : (
          <Card className="text-center py-4">
            <p className="text-muted mb-3">You haven't created any camps yet.</p>
            <a href="/create-camp" className="btn btn-primary">Create Your First Camp</a>
          </Card>
        )}
      </div>
    </div>
  );
}