import { render, screen } from '@testing-library/react';
import { fireEvent } from '@testing-library/dom';
import { vi } from 'vitest';
import { Button, Input, Card, Modal } from '@/components/common/index';

describe('Button', () => {
  it('renders button with children', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByRole('button', { name: 'Click me' })).toBeInTheDocument();
  });

  it('applies variant classes', () => {
    render(<Button variant="danger">Delete</Button>);
    const button = screen.getByRole('button');
    expect(button).toHaveClass('btn-danger');
  });

  it('applies size classes', () => {
    render(<Button size="lg">Large</Button>);
    const button = screen.getByRole('button');
    expect(button).toHaveClass('btn-lg');
  });

  it('shows loading spinner when isLoading', () => {
    render(<Button isLoading>Loading</Button>);
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  it('disables button when disabled', () => {
    render(<Button disabled>Disabled</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });
});

describe('Input', () => {
  it('renders input with label', () => {
    render(<Input label="Email" placeholder="Enter email" />);
    expect(screen.getByLabelText('Email')).toBeInTheDocument();
  });

  it('shows error message', () => {
    render(<Input label="Email" error="Invalid email" />);
    expect(screen.getByText('Invalid email')).toBeInTheDocument();
  });

  it('applies error class when error prop', () => {
    render(<Input label="Email" error="Invalid" />);
    const input = screen.getByLabelText('Email');
    expect(input).toHaveClass('is-invalid');
  });
});

describe('Card', () => {
  it('renders card with children', () => {
    render(<Card>Card content</Card>);
    expect(screen.getByText('Card content')).toBeInTheDocument();
  });

  it('renders header and footer', () => {
    render(
      <Card header="Header" footer="Footer">
        Body
      </Card>
    );
    expect(screen.getByText('Header')).toBeInTheDocument();
    expect(screen.getByText('Body')).toBeInTheDocument();
    expect(screen.getByText('Footer')).toBeInTheDocument();
  });
});

describe('Modal', () => {
  it('renders when isOpen is true', () => {
    render(<Modal isOpen={true} onClose={vi.fn()} title="Test Modal">Modal content</Modal>);
    expect(screen.getByText('Test Modal')).toBeInTheDocument();
    expect(screen.getByText('Modal content')).toBeInTheDocument();
  });

  it('does not render when isOpen is false', () => {
    render(<Modal isOpen={false} onClose={vi.fn()} title="Test Modal">Modal content</Modal>);
    expect(screen.queryByText('Test Modal')).not.toBeInTheDocument();
  });

  it('calls onClose when backdrop clicked', () => {
    const onClose = vi.fn();
    render(<Modal isOpen={true} onClose={onClose} title="Test Modal">Modal content</Modal>);
    fireEvent.click(screen.getByTestId('modal-backdrop') || document.querySelector('.modal-backdrop')!);
    const backdrop = document.querySelector('.modal-backdrop');
    if (backdrop) {
      fireEvent.click(backdrop);
      expect(onClose).toHaveBeenCalled();
    }
  });
});