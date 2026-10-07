import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

class ApiClient {
  private client: AxiosInstance;
  private isRefreshing = false;
  private failedQueue: Array<{
    resolve: (value: unknown) => void;
    reject: (reason: unknown) => void;
  }> = [];

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });

    this.client.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => config,
      (error: AxiosError) => Promise.reject(error)
    );

    this.client.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        if (error.response?.status === 401 && !originalRequest._retry) {
          // Don't redirect for /auth/me - let the AuthContext handle it gracefully
          const isAuthMeRequest = originalRequest.url?.includes('/auth/me');

          if (this.isRefreshing) {
            return new Promise((resolve, reject) => {
              this.failedQueue.push({ resolve, reject });
            })
              .then(() => this.client(originalRequest))
              .catch((err) => Promise.reject(err));
          }

          originalRequest._retry = true;
          this.isRefreshing = true;

          try {
            await this.refreshToken();
            this.processQueue(null, undefined);
            return this.client(originalRequest);
          } catch (refreshError) {
            this.processQueue(refreshError, undefined);
            this.clearAuth();
            // Only redirect to login if not the /auth/me request
            if (!isAuthMeRequest) {
              window.location.href = '/login';
            }
            return Promise.reject(refreshError);
          } finally {
            this.isRefreshing = false;
          }
        }

        return Promise.reject(error);
      }
    );
  }

  private processQueue(error: unknown, token: string | undefined) {
    this.failedQueue.forEach(({ resolve, reject }) => {
      if (error) {
        reject(error);
      } else {
        resolve(token);
      }
    });
    this.failedQueue = [];
  }

  private clearAuth() {
    localStorage.removeItem('user');
  }

  async refreshToken(): Promise<void> {
    await this.client.post('/auth/refresh');
  }

  // Auth endpoints
  async register(data: { email: string; password: string; name: string }) {
    const response = await this.client.post('/auth/register', data);
    return response.data;
  }

  async login(data: { email: string; password: string }) {
    const response = await this.client.post('/auth/login', data);
    return response.data;
  }

  async logout() {
    await this.client.post('/auth/logout');
    this.clearAuth();
  }

  async getMe() {
    const response = await this.client.get('/auth/me');
    return response.data;
  }

  // Event endpoints
  async getEvents(params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
    category?: string;
    search?: string;
    published?: boolean;
  }) {
    const response = await this.client.get('/events', { params });
    return response.data;
  }

  async getEvent(id: string) {
    const response = await this.client.get(`/events/${id}`);
    return response.data;
  }

  async createEvent(data: {
    title: string;
    description: string;
    category: string;
    startDate: string;
    endDate: string;
    location: string;
    capacity: number;
    published?: boolean;
  }) {
    const response = await this.client.post('/events', data);
    return response.data;
  }

  async updateEvent(id: string, data: Partial<{
    title: string;
    description: string;
    category: string;
    startDate: string;
    endDate: string;
    location: string;
    capacity: number;
    published: boolean;
  }>) {
    const response = await this.client.put(`/events/${id}`, data);
    return response.data;
  }

  async deleteEvent(id: string) {
    const response = await this.client.delete(`/events/${id}`);
    return response.data;
  }

  async getMyEvents(params?: { page?: number; size?: number }) {
    const response = await this.client.get('/events/my', { params });
    return response.data;
  }

  // Camp endpoints
  async getCamps(params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
    type?: string;
    search?: string;
    published?: boolean;
  }) {
    const response = await this.client.get('/camps', { params });
    return response.data;
  }

  async getCamp(id: string) {
    const response = await this.client.get(`/camps/${id}`);
    return response.data;
  }

  async createCamp(data: {
    title: string;
    description: string;
    type: string;
    startDate: string;
    endDate: string;
    location: string;
    ageGroup: string;
    capacity: number;
    price: number;
    published?: boolean;
  }) {
    const response = await this.client.post('/camps', data);
    return response.data;
  }

  async updateCamp(id: string, data: Partial<{
    title: string;
    description: string;
    type: string;
    startDate: string;
    endDate: string;
    location: string;
    ageGroup: string;
    capacity: number;
    price: number;
    published: boolean;
  }>) {
    const response = await this.client.put(`/camps/${id}`, data);
    return response.data;
  }

  async deleteCamp(id: string) {
    const response = await this.client.delete(`/camps/${id}`);
    return response.data;
  }

  async getMyCamps(params?: { page?: number; size?: number }) {
    const response = await this.client.get('/camps/my', { params });
    return response.data;
  }

  // Health endpoints
  async health() {
    const response = await this.client.get('/health');
    return response.data;
  }

  async ready() {
    const response = await this.client.get('/ready');
    return response.data;
  }
}

export const api = new ApiClient();