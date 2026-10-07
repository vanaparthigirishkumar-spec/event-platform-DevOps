import { createContext, useContext, useState, useCallback, ReactNode } from 'react';
import { Toast } from '../types';

interface ToastContextType {
  toasts: Toast[];
  showToast: (toast: Omit<Toast, 'id'>) => void;
  hideToast: (id: string) => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]);

  const showToast = useCallback((toast: Omit<Toast, 'id'>) => {
    const id = Math.random().toString(36).substring(2, 9);
    const newToast = { ...toast, id };
    setToasts((prev) => [...prev, newToast]);

    if (toast.duration !== 0) {
      setTimeout(() => {
        hideToast(id);
      }, toast.duration ?? 5000);
    }
  }, []);

  const hideToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  return (
    <ToastContext.Provider value={{ toasts, showToast, hideToast }}>
      {children}
      <ToastContainer toasts={toasts} onHide={hideToast} />
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
}

function ToastContainer({ toasts, onHide }: { toasts: Toast[]; onHide: (id: string) => void }) {
  return (
    <div className="position-fixed bottom-0 end-0 p-3" style={{ zIndex: 9999 }}>
      {toasts.map((toast) => (
        <ToastItem key={toast.id} toast={toast} onHide={onHide} />
      ))}
    </div>
  );
}

function ToastItem({ toast, onHide }: { toast: Toast; onHide: (id: string) => void }) {
  const icons = {
    success: '✓',
    error: '✕',
    info: 'ℹ',
    warning: '⚠',
  };

  return (
    <div
      className={`alert alert-${toast.type === 'error' ? 'danger' : toast.type} d-flex align-items-center gap-2 mb-2`}
      style={{ minWidth: '300px', boxShadow: '0 0.5rem 1rem rgba(0, 0, 0, 0.15)' }}
      role="alert"
    >
      <span className="flex-shrink-0" style={{ fontSize: '1.25rem' }}>{icons[toast.type]}</span>
      <div className="flex-grow-1">{toast.message}</div>
      <button
        type="button"
        className="btn-close"
        onClick={() => onHide(toast.id)}
        aria-label="Close"
      />
    </div>
  );
}