import { forwardRef, ButtonHTMLAttributes } from 'react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'outline';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className = '', variant = 'primary', size = 'md', isLoading, disabled, children, ...props }, ref) => {
    const baseClasses = 'btn d-inline-flex align-items-center justify-content-center gap-1';
    const variantClasses = {
      primary: 'btn-primary',
      secondary: 'btn-secondary',
      danger: 'btn-danger',
      outline: 'btn-outline',
    };
    const sizeClasses = {
      sm: 'btn-sm',
      md: '',
      lg: 'btn-lg',
    };

    return (
      <button
        ref={ref}
        className={`${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
        disabled={disabled || isLoading}
        {...props}
      >
        {isLoading && <span className="spinner" role="status" />}
        {children}
      </button>
    );
  }
);

Button.displayName = 'Button';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ className = '', label, error, helperText, id, ...props }, ref) => {
    const inputId = id || label?.toLowerCase().replace(/\s+/g, '-');

    return (
      <div className="form-group">
        {label && <label htmlFor={inputId} className="form-label">{label}</label>}
        <input
          ref={ref}
          id={inputId}
          className={`form-control ${error ? 'is-invalid' : ''} ${className}`}
          aria-invalid={!!error}
          aria-describedby={error ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined}
          {...props}
        />
        {error && <div id={`${inputId}-error`} className="invalid-feedback">{error}</div>}
        {helperText && !error && <div id={`${inputId}-helper`} className="form-text text-muted">{helperText}</div>}
      </div>
    );
  }
);

Input.displayName = 'Input';

export interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ className = '', label, error, helperText, id, ...props }, ref) => {
    const inputId = id || label?.toLowerCase().replace(/\s+/g, '-');

    return (
      <div className="form-group">
        {label && <label htmlFor={inputId} className="form-label">{label}</label>}
        <textarea
          ref={ref}
          id={inputId}
          className={`form-control ${error ? 'is-invalid' : ''} ${className}`}
          aria-invalid={!!error}
          aria-describedby={error ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined}
          {...props}
        />
        {error && <div id={`${inputId}-error`} className="invalid-feedback">{error}</div>}
        {helperText && !error && <div id={`${inputId}-helper`} className="form-text text-muted">{helperText}</div>}
      </div>
    );
  }
);

Textarea.displayName = 'Textarea';

export interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  options: { value: string; label: string }[];
  placeholder?: string;
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ className = '', label, error, options, placeholder, id, ...props }, ref) => {
    const inputId = id || label?.toLowerCase().replace(/\s+/g, '-');

    return (
      <div className="form-group">
        {label && <label htmlFor={inputId} className="form-label">{label}</label>}
        <select
          ref={ref}
          id={inputId}
          className={`form-control ${error ? 'is-invalid' : ''} ${className}`}
          aria-invalid={!!error}
          aria-describedby={error ? `${inputId}-error` : undefined}
          {...props}
        >
          {placeholder && <option value="" disabled>{placeholder}</option>}
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        {error && <div id={`${inputId}-error`} className="invalid-feedback">{error}</div>}
      </div>
    );
  }
);

Select.displayName = 'Select';

export interface CardProps {
  children: React.ReactNode;
  className?: string;
  style?: React.CSSProperties;
  header?: React.ReactNode;
  footer?: React.ReactNode;
}

export function Card({ children, className = '', style, header, footer }: CardProps) {
  return (
    <div className={`card ${className}`} style={style}>
      {header && <div className="card-header border-bottom mb-3 pb-2">{header}</div>}
      <div className="card-body">{children}</div>
      {footer && <div className="card-footer border-top mt-3 pt-2">{footer}</div>}
    </div>
  );
}

export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  size?: 'sm' | 'md' | 'lg' | 'xl';
  footer?: React.ReactNode;
}

export function Modal({ isOpen, onClose, title, children, size = 'md', footer }: ModalProps) {
  if (!isOpen) return null;

  const sizeClasses = {
    sm: 'modal-sm',
    md: '',
    lg: 'modal-lg',
    xl: 'modal-xl',
  };

  return (
    <div className="modal-backdrop fade show" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1050, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '1rem' }} onClick={onClose} data-testid="modal-backdrop">
      <div className={`modal ${sizeClasses[size]} show`} style={{ background: 'white', borderRadius: '0.5rem', boxShadow: '0 1rem 3rem rgba(0,0,0,0.175)', maxWidth: '100%', width: '100%', maxHeight: '90vh', overflow: 'auto' }} onClick={(e) => e.stopPropagation()}>
        <div className="modal-header d-flex align-items-center justify-content-between p-3 border-bottom">
          <h5 className="mb-0">{title}</h5>
          <button type="button" className="btn-close" onClick={onClose} aria-label="Close" />
        </div>
        <div className="modal-body p-3">{children}</div>
        {footer && <div className="modal-footer p-3 border-top">{footer}</div>}
      </div>
    </div>
  );
}

export interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

export function Spinner({ size = 'md', className = '' }: SpinnerProps) {
  const sizeClasses = {
    sm: 'spinner-border-sm',
    md: '',
    lg: 'spinner-border-lg',
  };

  return (
    <span className={`spinner ${sizeClasses[size]} ${className}`} role="status" aria-hidden="true">
      <span className="visually-hidden">Loading...</span>
    </span>
  );
}

export function LoadingOverlay({ isLoading, children }: { isLoading: boolean; children: React.ReactNode }) {
  if (!isLoading) return <>{children}</>;

  return (
    <div className="position-relative">
      {children}
      <div className="position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center" style={{ background: 'rgba(255,255,255,0.7)', zIndex: 10 }}>
        <Spinner size="lg" />
      </div>
    </div>
  );
}