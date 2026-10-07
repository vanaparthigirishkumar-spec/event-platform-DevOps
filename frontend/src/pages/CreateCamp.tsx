import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { api } from '../services/api';
import { Button, Input, Textarea, Select, Card } from '../components/common/index';
import { CampType } from '../types';
import { Link } from 'react-router-dom';

const createCampSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters').max(200),
  description: z.string().min(10, 'Description must be at least 10 characters').max(5000),
  type: z.enum(['DAY', 'OVERNIGHT', 'VIRTUAL']),
  startDate: z.string().min(1, 'Start date is required'),
  endDate: z.string().min(1, 'End date is required'),
  location: z.string().min(3, 'Location must be at least 3 characters').max(500),
  ageGroup: z.string().min(2, 'Age group is required').max(50),
  capacity: z.coerce.number().int().positive('Capacity must be a positive integer') as z.ZodNumber,
  price: z.coerce.number().min(0, 'Price cannot be negative') as z.ZodNumber,
  published: z.boolean().optional(),
}).refine((data) => new Date(data.endDate) > new Date(data.startDate), {
  message: 'End date must be after start date',
  path: ['endDate'],
});

type CreateCampFormData = z.infer<typeof createCampSchema>;

const types: { value: CampType; label: string }[] = [
  { value: 'DAY', label: 'Day Camp' },
  { value: 'OVERNIGHT', label: 'Overnight' },
  { value: 'VIRTUAL', label: 'Virtual' },
];

export function CreateCamp() {
  const { isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
  } = useForm<CreateCampFormData>({
    resolver: zodResolver(createCampSchema),
    defaultValues: {
      published: false,
    },
  });

  const startDate = watch('startDate');
  const endDate = watch('endDate');

  // Auto-set end date to 8 hours after start date if end date is empty or before start date
  useEffect(() => {
    if (startDate && (!endDate || new Date(endDate) <= new Date(startDate))) {
      const start = new Date(startDate);
      const end = new Date(start.getTime() + 8 * 60 * 60 * 1000);
      setValue('endDate', end.toISOString().slice(0, 16));
    }
  }, [startDate, endDate, setValue]);

  const onSubmit = async (data: CreateCampFormData) => {
    if (!isAuthenticated) {
      showToast({ type: 'error', message: 'You must be logged in to create a camp' });
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await api.createCamp(data);
      if (response.success) {
        showToast({ type: 'success', message: 'Camp created successfully!' });
        navigate(`/camps/${response.data.id}`);
      } else {
        throw new Error(response.error?.message || 'Failed to create camp');
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to create camp. Please try again.';
      showToast({ type: 'error', message });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="text-center py-5">
        <h3>Please log in to create a camp</h3>
        <Link to="/login" className="btn btn-primary mt-3">Login</Link>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: '700px' }}>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <h2 className="mb-0">Create Camp</h2>
        <Link to="/camps" className="btn btn-outline">Cancel</Link>
      </div>

      <Card>
        <form onSubmit={handleSubmit(onSubmit)} className="p-3" noValidate>
          <div className="mb-3">
            <label htmlFor="title" className="form-label">Title <span className="text-danger">*</span></label>
            <input
              {...register('title')}
              type="text"
              id="title"
              className={`form-control ${errors.title ? 'is-invalid' : ''}`}
              placeholder="Enter camp title"
              disabled={isSubmitting}
            />
            {errors.title && <div className="invalid-feedback">{errors.title.message}</div>}
          </div>

          <div className="mb-3">
            <label htmlFor="description" className="form-label">Description <span className="text-danger">*</span></label>
            <textarea
              {...register('description')}
              id="description"
              rows={5}
              className={`form-control ${errors.description ? 'is-invalid' : ''}`}
              placeholder="Describe your camp..."
              disabled={isSubmitting}
            />
            {errors.description && <div className="invalid-feedback">{errors.description.message}</div>}
          </div>

          <div className="row g-3 mb-3">
            <div className="col-md-6">
              <label htmlFor="type" className="form-label">Type <span className="text-danger">*</span></label>
              <select
                {...register('type')}
                id="type"
                className={`form-select ${errors.type ? 'is-invalid' : ''}`}
                disabled={isSubmitting}
              >
                <option value="">Select type</option>
                {types.map((t) => (
                  <option key={t.value} value={t.value}>{t.label}</option>
                ))}
              </select>
              {errors.type && <div className="invalid-feedback">{errors.type.message}</div>}
            </div>
            <div className="col-md-6">
              <label htmlFor="location" className="form-label">Location <span className="text-danger">*</span></label>
              <input
                {...register('location')}
                type="text"
                id="location"
                className={`form-control ${errors.location ? 'is-invalid' : ''}`}
                placeholder="Camp location"
                disabled={isSubmitting}
              />
              {errors.location && <div className="invalid-feedback">{errors.location.message}</div>}
            </div>
          </div>

          <div className="row g-3 mb-3">
            <div className="col-md-6">
              <label htmlFor="ageGroup" className="form-label">Age Group <span className="text-danger">*</span></label>
              <input
                {...register('ageGroup')}
                type="text"
                id="ageGroup"
                className={`form-control ${errors.ageGroup ? 'is-invalid' : ''}`}
                placeholder="e.g., 8-12, 13-17, 18+"
                disabled={isSubmitting}
              />
              {errors.ageGroup && <div className="invalid-feedback">{errors.ageGroup.message}</div>}
            </div>
            <div className="col-md-6">
              <label htmlFor="capacity" className="form-label">Capacity <span className="text-danger">*</span></label>
              <input
                {...register('capacity', { valueAsNumber: true })}
                type="number"
                id="capacity"
                min="1"
                className={`form-control ${errors.capacity ? 'is-invalid' : ''}`}
                placeholder="Number of attendees"
                disabled={isSubmitting}
              />
              {errors.capacity && <div className="invalid-feedback">{errors.capacity.message}</div>}
            </div>
          </div>

          <div className="row g-3 mb-3">
            <div className="col-md-6">
              <label htmlFor="price" className="form-label">Price <span className="text-danger">*</span></label>
              <input
                {...register('price', { valueAsNumber: true })}
                type="number"
                id="price"
                min="0"
                step="0.01"
                className={`form-control ${errors.price ? 'is-invalid' : ''}`}
                placeholder="0.00"
                disabled={isSubmitting}
              />
              {errors.price && <div className="invalid-feedback">{errors.price.message}</div>}
            </div>
            <div className="col-md-6">
              <div className="form-check mt-4">
                <input
                  {...register('published')}
                  type="checkbox"
                  id="published"
                  className="form-check-input"
                  disabled={isSubmitting}
                />
                <label className="form-check-label" htmlFor="published">Publish immediately</label>
              </div>
            </div>
          </div>

          <div className="row g-3 mb-3">
            <div className="col-md-6">
              <label htmlFor="startDate" className="form-label">Start Date & Time <span className="text-danger">*</span></label>
              <input
                {...register('startDate')}
                type="datetime-local"
                id="startDate"
                className={`form-control ${errors.startDate ? 'is-invalid' : ''}`}
                disabled={isSubmitting}
              />
              {errors.startDate && <div className="invalid-feedback">{errors.startDate.message}</div>}
            </div>
            <div className="col-md-6">
              <label htmlFor="endDate" className="form-label">End Date & Time <span className="text-danger">*</span></label>
              <input
                {...register('endDate')}
                type="datetime-local"
                id="endDate"
                className={`form-control ${errors.endDate ? 'is-invalid' : ''}`}
                disabled={isSubmitting}
              />
              {errors.endDate && <div className="invalid-feedback">{errors.endDate.message}</div>}
            </div>
          </div>

          <div className="d-flex gap-2 mt-4">
            <Button type="submit" className="flex-grow-1" size="lg" isLoading={isSubmitting}>
              Create Camp
            </Button>
            <Link to="/camps" className="btn btn-outline btn-lg">Cancel</Link>
          </div>
        </form>
      </Card>
    </div>
  );
}