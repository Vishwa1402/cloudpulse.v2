# CloudPulse AI Agent Engineering Rules

## Project Context

CloudPulse is an AI-Powered Incident Command Center built using:

- Spring Boot
- Angular
- PostgreSQL
- Prometheus
- Grafana
- Docker
- JWT Authentication

The system monitors real-time application metrics, detects incidents, and provides AI-assisted analysis.

The AI coding agent must prioritize:
- stability
- security
- maintainability
- observability
- scalability
- production-grade engineering practices

---

# Core Engineering Principles

## 1. Never Break Existing Functionality

Before modifying code:
- understand dependencies
- inspect related services/controllers/components
- avoid unnecessary refactors
- preserve API contracts unless explicitly instructed

Always:
- run/build affected modules
- ensure compilation succeeds
- ensure existing functionality still works

---

# 2. Follow Test-Driven Development (TDD)

The agent should strongly prefer TDD.

For every new feature:

## Workflow
1. Write failing test
2. Implement minimal working code
3. Refactor safely
4. Re-run all tests

---

## Backend Testing
also store the test results in a file against the test performed

Use:
- JUnit 5
- Mockito
- Spring Boot Test

Required tests:
- service layer tests
- controller tests
- repository tests
- security tests

Do NOT add untested business logic.

---

## Frontend Testing

Use:
- Jasmine
- Karma

Test:
- components
- services
- API integrations
- guards
- interceptors

---

# 3. Security Requirements

The agent must NEVER introduce security vulnerabilities.

---

## Secrets Handling

NEVER:
- hardcode API keys
- hardcode passwords
- hardcode JWT secrets
- commit credentials
- expose tokens in frontend code

Use:
- environment variables
- `.env`
- Spring configuration properties
- Docker secrets when applicable

---

## Sensitive Files

Never commit:
- `.env`
- secrets
- credentials
- access tokens
- private keys

Ensure `.gitignore` includes:
- `.env`
- `application-local.yml`
- `target/`
- `node_modules/`

---

## Authentication Rules

Always:
- validate JWT properly
- protect private endpoints
- use role-based authorization
- validate user input

Never:
- trust frontend validation alone
- expose admin APIs publicly

---

## Input Validation

Validate:
- request bodies
- query params
- path variables

Prevent:
- SQL injection
- XSS
- insecure deserialization

Use:
- DTO validation
- parameterized queries
- escaping/sanitization

---

# 4. Database Safety

Never:
- use raw SQL unnecessarily
- delete data without confirmation
- create destructive migrations automatically

Prefer:
- JPA repositories
- transactional operations
- indexed queries

All schema changes should be reversible.

---

# 5. Production-Grade Backend Standards

## API Standards

Use:
- RESTful naming
- DTOs
- proper HTTP status codes
- centralized exception handling

Example:
- 200 OK
- 201 CREATED
- 400 BAD REQUEST
- 401 UNAUTHORIZED
- 403 FORBIDDEN
- 404 NOT FOUND
- 500 INTERNAL SERVER ERROR

---

## Logging

Use structured logging.

Never log:
- passwords
- tokens
- secrets
- sensitive personal data

Log:
- incidents
- errors
- service failures
- important state changes

---

## Error Handling

Never expose:
- stack traces
- internal implementation details
- database internals

Use:
- global exception handlers
- sanitized error responses

---

# 6. Frontend Engineering Standards

## Angular Rules

Prefer:
- standalone components
- reusable services
- strongly typed interfaces
- reactive forms

Avoid:
- duplicated logic
- massive components
- hardcoded URLs

Use:
- environment configs
- interceptors
- guards

---

## UI Standards

Dashboard should feel production-grade:
- responsive
- clean
- real-time
- accessible

Avoid:
- cluttered layouts
- inconsistent styling
- unhandled loading states

---

# 7. Monitoring & Observability

The platform itself must be observable.

Ensure:
- health endpoints exist
- metrics are exposed
- errors are trackable
- incidents are auditable

Use:
- Spring Boot Actuator
- Prometheus metrics
- structured logs

---

# 8. Docker & Deployment Standards

Containers must:
- run independently
- use environment variables
- avoid hardcoded localhost assumptions

Use:
- Docker Compose
- health checks
- minimal images

---

# 9. Git Practices

The agent should:
- make small focused commits
- avoid unrelated changes
- preserve formatting consistency

Commit message style:
- feat:
- fix:
- refactor:
- test:
- docs:
- chore:

Example:
feat: add incident detection service

---

# 10. Code Quality Rules

Always:
- write readable code
- use meaningful names
- keep methods small
- avoid duplicated logic

Prefer:
- composition over inheritance
- interfaces where useful
- clean architecture principles

---

# 11. AI Feature Rules

AI-generated outputs must:
- never expose secrets
- never fabricate infrastructure state
- clearly separate facts vs suggestions

AI summaries should:
- be concise
- reference actual metrics
- explain possible causes carefully

---

# 12. Performance Considerations

Avoid:
- unnecessary polling
- N+1 queries
- blocking operations
- loading huge datasets

Prefer:
- pagination
- caching
- async processing
- WebSockets for real-time updates

---

# 13. Incident System Rules

Incidents must:
- be traceable
- include timestamps
- support severity levels
- maintain audit history

Severity examples:
- LOW
- MEDIUM
- HIGH
- CRITICAL

---

# 14. Architecture Expectations

Prefer layered architecture:

Controller
→ Service
→ Repository
→ Database

Avoid:
- business logic inside controllers
- direct DB access from controllers

---

# 15. Before Any Merge

The agent must verify:

## Backend
- project builds
- tests pass
- no security leaks
- no broken endpoints

## Frontend
- app compiles
- no console errors
- routes work
- API integrations succeed

---

# 16. Long-Term Goal

CloudPulse should evolve into a production-grade:
- observability platform
- incident management system
- AI-assisted operations dashboard

The codebase should remain:
- modular
- secure
- scalable
- maintainable
- deployment-ready
