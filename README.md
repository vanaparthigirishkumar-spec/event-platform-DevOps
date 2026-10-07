# FLM Cloud-Native Platform

Production-grade event platform with React frontend, Spring Boot backend, and MongoDB.

## Quick Start

```bash
# Start the full stack
./scripts/start.sh

# Check status
./scripts/status.sh

# Check health
./scripts/health.sh

# View logs
./scripts/logs.sh [service] [--follow]

# Stop the stack
./scripts/stop.sh

# Restart services
./scripts/restart.sh [service]

# Clean environment
./scripts/clean.sh [--all] [--images] [--volumes]

# Troubleshooting
./scripts/troubleshoot.sh
```

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    Frontend     │────▶│    Backend      │────▶│    MongoDB      │
│   (Nginx)       │:3000│  (Spring Boot)  │:8080│    (7.0)        │
│   React/TS      │     │   Java 21       │     │   Replica Set   │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

## Services

| Service | Port | Health | Ready |
|---------|------|--------|-------|
| Frontend | 3000 | `/health` | - |
| Backend | 8080 | `/health` | `/ready` |
| MongoDB | 27017 | - | - |

## API Endpoints

### Events
- `GET /api/events` - List events (paginated)
- `GET /api/events/:id` - Get event details
- `POST /api/events` - Create event (auth required)
- `PUT /api/events/:id` - Update event (auth required)
- `DELETE /api/events/:id` - Delete event (auth required)

### Camps
- `GET /api/camps` - List camps (paginated)
- `GET /api/camps/:id` - Get camp details
- `POST /api/camps` - Create camp (auth required)
- `PUT /api/camps/:id` - Update camp (auth required)
- `DELETE /api/camps/:id` - Delete camp (auth required)

### Auth
- `POST /api/auth/register` - Register user
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Logout
- `GET /api/auth/me` - Get current user

## Scripts Reference

### `./scripts/start.sh`
Starts the full stack with health checks.
- Builds Docker images
- Starts containers in dependency order
- Waits for all health checks to pass
- Verifies endpoints

### `./scripts/stop.sh [--remove-volumes]`
Stops the stack.
- `--remove-volumes`: Also removes MongoDB data volume (destructive)

### `./scripts/restart.sh [service]`
Restarts one or all services.
- No argument: restarts all services
- With service name: restarts only that service

### `./scripts/status.sh`
Shows container status and health details.

### `./scripts/health.sh`
Checks all application health endpoints.
- Backend `/health` and `/ready`
- Frontend `/health`
- Frontend proxy to backend `/health` and `/ready`
- API connectivity for `/api/events` and `/api/camps`

### `./scripts/logs.sh [service] [--follow] [--tail N]`
View container logs.
- No service: shows all services
- `--follow`: follow log output (like `tail -f`)
- `--tail N`: show last N lines (default: 100)

### `./scripts/clean.sh [--all] [--images] [--volumes]`
Cleans the local environment.
- Default: stops containers, removes build artifacts
- `--images`: also removes Docker images
- `--volumes`: also removes MongoDB data volume (destructive)
- `--all`: combines --images and --volumes

### `./scripts/troubleshoot.sh`
Generates a comprehensive troubleshooting report:
- Docker version and daemon status
- Container status, health, restart counts
- Network and volume info
- Port bindings
- Health endpoint checks
- Recent logs from all services
- Disk usage

## Development

### Backend
```bash
cd backend
./mvnw test              # Run tests
./mvnw clean package     # Build JAR
```

### Frontend
```bash
cd frontend
npm run test             # Run tests (Vitest)
npm run typecheck        # TypeScript check
npm run build            # Production build
npm run dev              # Dev server
```

## Docker

```bash
docker compose config     # Validate compose file
docker compose build      # Build all images
docker compose up -d      # Start detached
docker compose down       # Stop and remove containers
docker compose down -v    # Stop and remove volumes
```

## Environment Variables

Key variables (set in `.env` or shell):

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | JWT signing secret | (required in production) |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `docker` |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection | `mongodb://mongodb:27017/eventplatform` |
| `VITE_API_URL` | Frontend API base URL | `/api` (proxied via Nginx) |

## AWS / Terraform

### Infrastructure Architecture (Phase 6)

```
┌─────────────────────────────────────────────────────────────┐
│                        AWS Cloud                              │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ VPC (10.0.0.0/16)                                       │ │
│  │  ┌──────────────┐  ┌──────────────┐                     │ │
│  │  │ Public Subnet │  │ Public Subnet │  (2 AZs)          │ │
│  │  │ 10.0.1.0/24  │  │ 10.0.2.0/24  │                     │ │
│  │  │ (NAT Gateway) │  │              │                     │ │
│  │  └──────────────┘  └──────────────┘                     │ │
│  │  ┌──────────────┐  ┌──────────────┐                     │ │
│  │  │ Private Subnet│  │ Private Subnet│  (2 AZs)          │ │
│  │  │ 10.0.11.0/24 │  │ 10.0.12.0/24 │                     │ │
│  │  │  (EKS Nodes)  │  │  (EKS Nodes)  │                     │ │
│  │  └──────────────┘  └──────────────┘                     │ │
│  └─────────────────────────────────────────────────────────┘ │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   ECR       │  │   EKS       │  │   MongoDB Atlas     │  │
│  │  (Images)   │  │  (Cluster)  │  │  (Database)         │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Terraform Structure

```
terraform/
├── main.tf                    # Root module composition
├── providers.tf               # AWS, Kubernetes, Helm providers
├── variables.tf               # Input variables
├── locals.tf                  # Tagging, naming, AZs
├── outputs.tf                 # Root outputs
├── backend/main.tf            # S3 backend config
├── environments/
│   ├── dev/main.tf            # Dev environment
│   └── prod/main.tf           # Prod environment
└── modules/
    ├── vpc/                   # VPC, subnets, IGW, NAT, routes
    ├── iam/                   # EKS cluster/node roles, policies
    ├── ecr/                   # ECR repos (backend/frontend)
    └── eks/                   # EKS cluster + managed node group
```

### AWS Resources

| Component | Configuration |
|-----------|---------------|
| VPC | 10.0.0.0/16, 2 AZs |
| Public Subnets | 10.0.1.0/24, 10.0.2.0/24 |
| Private Subnets | 10.0.11.0/24, 10.0.12.0/24 |
| NAT Gateway | 1 (cost-conscious) |
| ECR Repositories | backend, frontend (immutable tags, scan on push) |
| EKS Version | 1.29 |
| Node Group | t3.medium, 1-4 nodes (dev: 2 desired) |
| MongoDB | Atlas (external) |

### Terraform State

- **Backend**: S3 (`flm-terraform-state`)
- **Locking**: Native S3 state locking (no DynamoDB)
- **Environments**: Separate state keys (`dev/`, `prod/`)

### Prerequisites

```bash
# AWS CLI configured with appropriate credentials
aws sts get-caller-identity

# S3 bucket for state exists
aws s3 ls s3://flm-terraform-state

# Terraform installed
terraform version  # >= 1.6.0
```

### Deployment Commands

```bash
cd terraform/environments/dev
terraform init          # Initialize (creates .terraform.lock.hcl)
terraform fmt -check -recursive
terraform validate
terraform plan          # Review changes
terraform apply         # Deploy (requires AWS credentials)
```

### Cleanup

```bash
cd terraform/environments/dev
terraform destroy       # Remove all resources
```

### Cost Considerations

- **2 AZs** for HA (not 3)
- **1 NAT Gateway** (not 1 per AZ)
- **t3.medium** nodes (not larger)
- **Spot instances** recommended for production node groups
- **MongoDB Atlas** free tier for development

---

### Frontend Blank Page Fix

The frontend initially showed a blank page due to an axios interceptor redirecting on 401 responses. The `/api/auth/me` endpoint returns 401 when unauthenticated, but the interceptor was redirecting to `/login` before the AuthContext could handle it gracefully.

**Fix applied in `frontend/src/services/api.ts`:**
- Added check to skip redirect for `/auth/me` requests
- AuthContext now handles 401 gracefully by setting user to null

```typescript
const isAuthMeRequest = originalRequest.url?.includes('/auth/me');
// ... only redirect if not the auth/me request
if (!isAuthMeRequest) {
  window.location.href = '/login';
}
```

---

## Security

### Authentication
- JWT access tokens (15 min expiry)
- JWT refresh tokens (7 day expiry)
- BCrypt password hashing (cost 12)
- Roles: USER, ORGANIZER, ADMIN

### Authorization
- Resource ownership checks
- Role-based access control
- Method-level security with `@PreAuthorize`

## Testing

### Backend (32 tests)
- Unit tests for services
- Integration tests for controllers
- Security tests

### Frontend (13 tests)
- Component tests (Button, Input, Card, Modal)
- Vitest with React Testing Library

## CI/CD

See `Jenkinsfile` for the CI pipeline:
1. Checkout
2. Backend build & test
3. Frontend install, typecheck, test, build
4. SonarQube analysis
5. Docker image build
6. Trivy vulnerability scan
7. Image tagging with commit SHA

## License

MIT

## Security (DevSecOps)

### SonarQube - Static Code Analysis

**Why SonarQube?**
- Detects bugs, vulnerabilities, and code smells early
- Enforces coding standards across the team
- Tracks technical debt over time
- Integrates with CI/CD for quality gates

**Configuration:**
- Backend: `backend/sonar-project.properties`
- Frontend: `frontend/sonar-project.properties`
- Excludes generated code, build artifacts, and test files from analysis
- Quality gate configured to fail on critical issues

**When Pipeline Fails:**
- New critical vulnerabilities introduced
- Major code smells exceeding thresholds
- Test coverage below minimum threshold
- Duplicated code blocks exceeding limits

### Trivy - Container Vulnerability Scanning

**Why Trivy?**
- Scans OS packages and language-specific dependencies
- Fast, comprehensive vulnerability database
- Integrates easily into CI pipelines
- Supports multiple output formats

**Configuration:**
- Scans both backend and frontend Docker images
- Fails pipeline only on HIGH and CRITICAL severity
- Uses `.trivyignore` for documented exceptions
- Scans run after Docker image build

**When Pipeline Fails:**
- HIGH or CRITICAL vulnerabilities in base images
- HIGH or CRITICAL vulnerabilities in application dependencies
- New vulnerabilities introduced since last scan

### Vulnerability Investigation Process

1. **Identify**: Pipeline fails on SonarQube or Trivy stage
2. **Analyze**: Review scan reports in Jenkins artifacts
3. **Assess**: Determine exploitability in your context
4. **Remediate**: 
   - Update base images (Dockerfile FROM)
   - Update dependencies (pom.xml, package.json)
   - Apply patches or workarounds
5. **Document**: Add justified exceptions to `.trivyignore` if needed
6. **Verify**: Re-run pipeline to confirm fix

### Security Best Practices

- Never hardcode secrets in code or Dockerfiles
- Use Jenkins credentials for all sensitive values
- Rotate JWT secrets regularly in production
- Keep base images updated (monthly minimum)
- Monitor dependency updates with Dependabot/Renovate
- Run Trivy scans locally before pushing: `trivy image <image:tag>`