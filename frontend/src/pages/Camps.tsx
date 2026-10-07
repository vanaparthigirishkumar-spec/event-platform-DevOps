import { useState, useEffect } from 'react';
import { useCamps } from '../hooks/useApi';
import { CampList, CampCard } from '../components/camps/index';
import { Button, Card, Spinner, Input, Select } from '../components/common/index';
import { CampType } from '../types';
import { Link } from 'react-router-dom';

export function Camps() {
  const [params, setParams] = useState({
    page: 0,
    size: 10,
    sortBy: 'startDate',
    sortOrder: 'asc' as 'asc' | 'desc',
    type: undefined as CampType | undefined,
    search: '',
    published: true,
  });
  const [searchInput, setSearchInput] = useState('');

  const { data, isLoading, error, refetch } = useCamps(params);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setParams((prev) => ({ ...prev, page: 0, search: searchInput }));
  };

  const handleTypeChange = (value: string) => {
    setParams((prev) => ({ ...prev, page: 0, type: (value as CampType) || undefined }));
  };

  const handlePageChange = (page: number) => {
    setParams((prev) => ({ ...prev, page }));
  };

  const types: { value: CampType; label: string }[] = [
    { value: 'DAY', label: 'Day Camp' },
    { value: 'OVERNIGHT', label: 'Overnight' },
    { value: 'VIRTUAL', label: 'Virtual' },
  ];

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <h2 className="mb-0">Camps</h2>
        <Link to="/create-camp" className="btn btn-primary">Create Camp</Link>
      </div>

      <Card className="mb-4">
        <div className="card-body">
          <form onSubmit={handleSearch} className="row g-3 align-items-end">
            <div className="col-12 col-md-6">
              <label className="form-label">Search</label>
              <Input
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                placeholder="Search camps..."
              />
            </div>
            <div className="col-12 col-md-3">
              <label className="form-label">Type</label>
              <Select
                value={params.type || ''}
                onChange={(e) => handleTypeChange(e.target.value)}
                options={[{ value: '', label: 'All Types' }, ...types]}
                placeholder="All Types"
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
          Failed to load camps: {error.message}
          <Button variant="outline" className="ms-2" onClick={refetch}>Retry</Button>
        </div>
      ) : (
        <>
          <CampList camps={data?.content || []} onCampClick={(camp) => window.location.href = `/camps/${camp.id}`} emptyMessage="No camps found matching your criteria." />
          {data && data.totalPages > 1 && (
            <nav className="mt-4" aria-label="Camp pagination">
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