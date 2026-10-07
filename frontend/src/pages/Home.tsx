import { Link } from 'react-router-dom';
import { Button, Card } from '../components/common/index';

export function Home() {
  return (
    <div className="min-vh-100 d-flex flex-column">
      <section className="py-5 bg-light">
        <div className="container text-center py-5">
          <h1 className="display-3 fw-bold mb-4">EventPlatform</h1>
          <p className="lead text-muted mb-5 max-w-2xl mx-auto">
            Discover, create, and manage events and camps. Connect with your community through meaningful experiences.
          </p>
          <div className="d-flex gap-3 justify-content-center flex-wrap">
            <Link to="/events" className="btn btn-primary btn-lg px-4">Explore Events</Link>
            <Link to="/camps" className="btn btn-outline btn-lg px-4">Browse Camps</Link>
          </div>
        </div>
      </section>

      <section className="py-5">
        <div className="container">
          <div className="row g-4 text-center">
            <div className="col-12 col-md-4">
              <Card className="h-100 py-4">
                <div className="display-4 text-primary mb-3">
                  <i className="bi bi-calendar-event"></i>
                </div>
                <h4>Events</h4>
                <p className="text-muted">Discover local events, conferences, workshops, and meetups</p>
              </Card>
            </div>
            <div className="col-12 col-md-4">
              <Card className="h-100 py-4">
                <div className="display-4 text-success mb-3">
                  <i className="bi bi-tent"></i>
                </div>
                <h4>Camps</h4>
                <p className="text-muted">Find day camps, overnight adventures, and virtual experiences</p>
              </Card>
            </div>
            <div className="col-12 col-md-4">
              <Card className="h-100 py-4">
                <div className="display-4 text-info mb-3">
                  <i className="bi bi-plus-circle"></i>
                </div>
                <h4>Create</h4>
                <p className="text-muted">Organize your own events and camps for the community</p>
              </Card>
            </div>
          </div>
        </div>
      </section>

      <section className="py-5 bg-light">
        <div className="container text-center">
          <h2 className="mb-4">Ready to get started?</h2>
          <p className="text-muted mb-4">Join thousands of organizers and attendees on EventPlatform</p>
          <div className="d-flex gap-3 justify-content-center flex-wrap">
            <Link to="/register" className="btn btn-primary btn-lg px-5">Create Free Account</Link>
            <Link to="/login" className="btn btn-outline btn-lg px-5">Login</Link>
          </div>
        </div>
      </section>
    </div>
  );
}