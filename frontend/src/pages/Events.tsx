import { useState, useEffect } from 'react';
import { useEvents } from '../hooks/useApi';
import { EventList, EventCard } from '../components/events/index';
import { Button, Card, Spinner, Input, Select } from '../components/common/index';
import { EventCategory } from '../types';

export function Events() {
  const [params, setParams] = useState({
    page: 0,
    size: 10,
    sortBy: 'startDate',
    sortOrder: 'asc' as 'asc' | 'desc',
    category: undefined as EventCategory | undefined,
    search: '',
    published: true,
  });
  const [searchInput, setSearchInput] = useState('');

  const { data, isLoading, error, refetch } = useEvents(params);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setParams((prev) => ({ ...prev, page: 0, search: searchInput }));
  };

  const handleCategoryChange = (value: string) => {
    setParams((prev) => ({ ...prev, page: 0, category: (value as EventCategory) || undefined }));
  };

  const handleSortChange = (sortBy: string) => {
    setParams((prev) => ({
      ...prev,
      page: 0,
      sortBy,
      sortOrder: prev.sortBy === sortBy && prev.sortOrder === 'asc' ? 'desc' : 'asc',
    }));
  };

  const handlePageChange = (page: number) => {
    setParams((prev) => ({ ...prev, page }));
  };

  const categories: { value: EventCategory; label: string }[] = [
    { value: 'TECHNOLOGY', label: 'Technology' },
    { value: 'BUSINESS', label: 'Business' },
    { value: 'EDUCATION', label: 'Education' },
    { value: 'HEALTH', label: 'Health' },
    { value: 'ARTS', label: 'Arts' },
    { value: 'SPORTS', label: 'Sports' },
    { value: 'OTHER', label: 'Other' },
  ];

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <h2 className="mb-0">Events</h2>
        <Link to="/create-event" className="btn btn-primary">Create Event</Link>
      </div>

      <Card className="mb-4">
        <div className="card-body">
          <form onSubmit={handleSearch} className="row g-3 align-items-end">
            <div className="col-12 col-md-6">
              <label className="form-label">Search</label>
              <Input
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                placeholder="Search events..."
              />
            </div>
            <div className="col-12 col-md-3">
              <label className="form-label">Category</label>
              <Select
                value={params.category || ''}
                onChange={(e) => handleCategoryChange(e.target.value)}
                options={[{ value: '', label: 'All Categories' }, ...categories]}
                placeholder="All Categories"
              />
            </div>
            <div className="col-12 col-md-3 d-flex">
              <Button type="submit" className="w-100">Search</Button>
            </div>
          </form>
        </div>
      </Card>

      {isLoading ? (
        <div className="d-flex align-items-center justify-content-center py-5">
          <Spinner size="lg" />
        </div>
      ) : error ? (
        <div className="alert alert-danger" role="alert">
          Failed to load events: {error.message}
          <Button variant="outline" className="ms-2" onClick={refetch}>Retry</Button>
        </div>
      ) : (
        <>
          <EventList events={data?.content || []} onEventClick={(event) => window.location.href = `/events/${event.id}`} emptyMessage="No events found matching your criteria." />
          {data && data.totalPages > 1 && (
            <nav className="mt-4" aria-label="Event pagination">
              <ul className="pagination justify-content-center">
                <li className={`page-item ${params.page === 0 ? 'disabled' : ''}`}>
                  <button className="page-link" onClick={() => handlePageChange(params.page - 1)} disabled={params.page === 0} aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                  </button>
                </li>
                {Array.from({ length: Math.min(data.totalPages, 5) }, (_, i) => {
                  let pageNum = 0;
                  if (data.totalPages <= 5) {
                    pageNum = i;
                  } else if (params.page <= 2) {
                    pageNum = i;
                  } else if (params.page >= data.totalPages - 3) {
                    pageNum = data.totalPages - 5 + i;
                  } else {
                    pageNum = params.page - 2 + i;
                  }
                  return (
                    <li key={pageNum} className={`page-item ${params.page === pageNum ? 'active' : ''}`}>
                      <button className="page-link" onClick={() => handlePageChange(pageNum)}>{pageNum + 1}</button>
                    </li>
                  );
                })}
                <li className={`page-item ${params.page === data.totalPages - 1 ? 'disabled' : ''}`}>
                  <button className="page-link" onClick={() => handlePageChange(params.page + 1)} disabled={params.page === data.totalPages - 1} aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                  </button>
                </li>
              </ul>
            </nav>
          )}
        </>
      )}
    </div>
  );
}

import { Link } from 'react-router-dom';