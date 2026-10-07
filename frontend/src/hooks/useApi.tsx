import { useState, useEffect, useCallback } from 'react';
import { api } from '../services/api';
import { Event, Camp, PageResponse, EventQueryParams, CampQueryParams } from '../types';

export function useEvents(params?: EventQueryParams) {
  const [data, setData] = useState<PageResponse<Event> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const fetchEvents = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await api.getEvents(params);
      if (response.success) {
        setData(response.data);
      } else {
        throw new Error(response.error?.message || 'Failed to fetch events');
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Unknown error'));
    } finally {
      setIsLoading(false);
    }
  }, [params]);

  useEffect(() => {
    fetchEvents();
  }, [fetchEvents]);

  return { data, isLoading, error, refetch: fetchEvents };
}

export function useEvent(id: string | undefined) {
  const [data, setData] = useState<Event | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    if (!id) return;

    let cancelled = false;
    setIsLoading(true);
    setError(null);

    api.getEvent(id)
      .then((response) => {
        if (!cancelled) {
          if (response.success) {
            setData(response.data);
          } else {
            throw new Error(response.error?.message || 'Failed to fetch event');
          }
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof Error ? err : new Error('Unknown error'));
        }
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false);
      });

    return () => { cancelled = true; };
  }, [id]);

  return { data, isLoading, error };
}

export function useMyEvents(params?: { page?: number; size?: number }) {
  const [data, setData] = useState<PageResponse<Event> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const fetchEvents = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await api.getMyEvents(params);
      if (response.success) {
        setData(response.data);
      } else {
        throw new Error(response.error?.message || 'Failed to fetch events');
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Unknown error'));
    } finally {
      setIsLoading(false);
    }
  }, [params]);

  useEffect(() => {
    fetchEvents();
  }, [fetchEvents]);

  return { data, isLoading, error, refetch: fetchEvents };
}

export function useCamps(params?: CampQueryParams) {
  const [data, setData] = useState<PageResponse<Camp> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const fetchCamps = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await api.getCamps(params);
      if (response.success) {
        setData(response.data);
      } else {
        throw new Error(response.error?.message || 'Failed to fetch camps');
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Unknown error'));
    } finally {
      setIsLoading(false);
    }
  }, [params]);

  useEffect(() => {
    fetchCamps();
  }, [fetchCamps]);

  return { data, isLoading, error, refetch: fetchCamps };
}

export function useCamp(id: string | undefined) {
  const [data, setData] = useState<Camp | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    if (!id) return;

    let cancelled = false;
    setIsLoading(true);
    setError(null);

    api.getCamp(id)
      .then((response) => {
        if (!cancelled) {
          if (response.success) {
            setData(response.data);
          } else {
            throw new Error(response.error?.message || 'Failed to fetch camp');
          }
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof Error ? err : new Error('Unknown error'));
        }
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false);
      });

    return () => { cancelled = true; };
  }, [id]);

  return { data, isLoading, error };
}

export function useMyCamps(params?: { page?: number; size?: number }) {
  const [data, setData] = useState<PageResponse<Camp> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const fetchCamps = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await api.getMyCamps(params);
      if (response.success) {
        setData(response.data);
      } else {
        throw new Error(response.error?.message || 'Failed to fetch camps');
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Unknown error'));
    } finally {
      setIsLoading(false);
    }
  }, [params]);

  useEffect(() => {
    fetchCamps();
  }, [fetchCamps]);

  return { data, isLoading, error, refetch: fetchCamps };
}