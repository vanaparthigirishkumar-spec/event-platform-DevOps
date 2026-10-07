export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  createdAt: string;
}

export type UserRole = 'USER' | 'ORGANIZER' | 'ADMIN';

export interface Event {
  id: string;
  title: string;
  description: string;
  category: EventCategory;
  startDate: string;
  endDate: string;
  location: string;
  capacity: number;
  availableSpots: number;
  organizer: OrganizerInfo;
  published: boolean;
  createdAt: string;
  updatedAt: string;
}

export type EventCategory = 
  | 'TECHNOLOGY' 
  | 'BUSINESS' 
  | 'EDUCATION' 
  | 'HEALTH' 
  | 'ARTS' 
  | 'SPORTS' 
  | 'OTHER';

export interface OrganizerInfo {
  id: string;
  name: string;
  email: string;
}

export interface Camp {
  id: string;
  title: string;
  description: string;
  type: CampType;
  startDate: string;
  endDate: string;
  location: string;
  ageGroup: string;
  capacity: number;
  availableSpots: number;
  price: number;
  organizer: OrganizerInfo;
  published: boolean;
  createdAt: string;
  updatedAt: string;
}

export type CampType = 'DAY' | 'OVERNIGHT' | 'VIRTUAL';

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface EventQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
  category?: EventCategory;
  search?: string;
  published?: boolean;
}

export interface CampQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
  type?: CampType;
  search?: string;
  published?: boolean;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface ApiError {
  success: false;
  error: {
    message: string;
    details?: Record<string, string>;
  };
  status: number;
}

export interface Toast {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
  duration?: number;
}