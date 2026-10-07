import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { api } from '../services/api';
import { Button, Input, Textarea, Select, Card } from '../components/common/index';
import { EventCategory } from '../types';

const createEventSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters').max(200),
  description: z.string().min(10, 'Description must be at least 10 characters').max(5000),
  category: z.enum(['TECHNOLOGY', 'BUSINESS', 'EDUCATION', 'HEALTH', 'ARTS', 'SPORTS', 'OTHER']),
  startDate: z.string().min(1, 'Start date is required'),
  endDate: z.string().min(1, 'End date is required'),
  location: z.string().min(3, 'Location must be at least 3 characters').max(500),
  capacity: z.coerce.number().int().positive('Capacity must be a positive integer') as z.ZodNumber,
  published: z.boolean().optional(),
}).refine((data) => new Date(data.endDate) > new Date(data.startDate), {
  message: 'End date must be after start date',
  path: ['endDate'],
});

type CreateEventFormData = z.infer<typeof createEventSchema>;

const categories: { value: EventCategory; label: string }[] = [
  { value: 'TECHNOLOGY', label: 'Technology' },
  { value: 'BUSINESS', label: 'Business' },
  { value: 'EDUCATION', label: 'Education' },
  { value: 'HEALTH', label: 'Health' },
  { value: 'ARTS', label: 'Arts' },
  { value: 'SPORTS', label: 'Sports' },
  { value: 'OTHER', label: 'Other' },
];

export function CreateEvent() {
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
  } = useForm<CreateEventFormData>({
    resolver: zodResolver(createEventSchema),
    defaultValues: {
      published: false,
    },
  });

  const startDate = watch('startDate');
  const endDate = watch('endDate');

  // Auto-set end date to 2 hours after start date if end date is empty or before start date
  useEffect(() => {
    if (startDate && (!endDate || new Date(endDate) <= new Date(startDate))) {
      const start = new Date(startDate);
      const end = new Date(start.getTime() + 2 * 60 * 60 * 1000);
      setValue('endDate', end.toISOString().slice(0, 16));
    }
  }, [startDate, endDate, setValue]);

  const onSubmit = async (data: CreateEventFormData) => {
    if (!isAuthenticated) {
      showToast({ type: 'error', message: 'You must be logged in to create an event' });
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await api.createEvent(data);
      if (response.success) {
        showToast({ type: 'success', message: 'Event created successfully!' });
        navigate(`/events/${response.data.id}`);
      } else {
        throw new Error(response.error?.message || 'Failed to create event');
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to create event. Please try again.';
      showToast({ type: 'error', message });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="text-center py-5">
        <h3>Please log in to create an event</h3>
        <Link to="/login" className="btn btn-primary mt-3">Login</Link>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: '700px' }}>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <h2 className="mb-0">Create Event</h2>
        <Link to="/events" className="btn btn-outline">Cancel</Link>
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
              placeholder="Enter event title"
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
              placeholder="Describe your event..."
              disabled={isSubmitting}
            />
            {errors.description && <div className="invalid-feedback">{errors.description.message}</div>}
          </div>

          <div className="row g-3 mb-3">
            <div className="col-md-6">
              <label htmlFor="category" className="form-label">Category <span className="text-danger">*</span></label>
              <select
                {...register('category')}
                id="category"
                className={`form-select ${errors.category ? 'is-invalid' : ''}`}
                disabled={isSubmitting}
              >
                <option value="">Select category</option>
                {categories.map((cat) => (
                  <option key={cat.value} value={cat.value}>{cat.label}</option>
                ))}
              </select>
              {errors.category && <div className="invalid-feedback">{errors.category.message}</div>}
            </div>
            <div className="col-md-6">
              <label htmlFor="location" className="form-label">Location <span className="text-danger">*</span></label>
              <input
                {...register('location')}
                type="text"
                id="location"
                className={`form-control ${errors.location ? 'is-invalid' : ''}`}
                placeholder="Event location"
                disabled={isSubmitting}
              />
              {errors.location && <div className="invalid-feedback">{errors.location.message}</div>}
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

          <div className="row g-3 mb-3">
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

          <div className="d-flex gap-2 mt-4">
            <Button type="submit" className="flex-grow-1" size="lg" isLoading={isSubmitting}>
              Create Event
            </Button>
            <Link to="/events" className="btn btn-outline btn-lg">Cancel</Link>
          </div>
        </form>
      </Card>
    </div>
  );
}

import { useEffect } from 'react';
import { Link } from 'react-router-dom';