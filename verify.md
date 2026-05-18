# CloudPulse Feature & Schema Verification Guide

## Purpose

This document defines the verification rules for:
- database schema
- backend APIs
- frontend features
- monitoring integrations
- security
- observability
- production readiness

The AI coding agent MUST validate all new implementations against this document before modifying the project.

---

# Verification Workflow

Before implementing any feature:

1. Analyze current architecture
2. Verify schema consistency
3. Check feature dependencies
4. Identify affected modules
5. Ensure no breaking changes
6. Ensure security compliance
7. Ensure production-grade implementation

After implementation:

1. Run tests
2. Verify API contracts
3. Verify frontend integration
4. Verify database integrity
5. Verify Docker compatibility
6. Verify no secrets are exposed
7. Verify application still builds successfully

---

# Core System Modules

The platform consists of:

- Authentication & Authorization
- Organizations & Multi-Tenancy
- Projects & Services
- Monitoring & Metrics
- Incident Management
- Alerts & Notifications
- AI Analysis
- Audit Logging
- Real-Time Dashboard
- Infrastructure Integrations

All new features must align with this architecture.

---

# Database Schema Verification Rules

---

## 1. Naming Standards

### Tables
Use:
- lowercase
- snake_case
- plural names

Examples:
```text
users
incident_events
alert_rules
```

---

### Columns
Use:
- snake_case
- descriptive names

Examples:
```text
created_at
updated_at
service_id
organization_id
```

---

# 2. Required Standard Columns

Every major table should contain:

```text
id
created_at
updated_at
```

Optional:
```text
created_by
deleted_at
```

---

# 3. Foreign Key Rules

All relationships must:
- use foreign keys
- enforce referential integrity
- define cascade behavior carefully

Example:
```text
project_id → projects.id
service_id → services.id
```

---

# 4. Multi-Tenancy Validation

All tenant-scoped data must include:

```text
organization_id
```

The agent must verify:
- data isolation
- organization ownership
- access control

No tenant should access another tenant's data.

---

# 5. Migration Safety Rules

The agent must NEVER:
- drop production tables automatically
- delete columns without verification
- create destructive migrations

Safe migrations only.

---

# Feature Verification Matrix

---

# Authentication Module

## Verify:
- JWT authentication works
- refresh token flow works
- passwords are hashed
- role-based access works
- protected routes require authentication

---

## Required Tables

```text
users
roles
refresh_tokens
organizations
```

---

# Project & Service Management

## Verify:
- users can create projects
- services belong to projects
- organization isolation exists
- APIs validate ownership

---

## Required Tables

```text
projects
services
```

---

# Metrics & Monitoring

## Verify:
- Prometheus metrics are accessible
- metrics are refreshed properly
- dashboard shows real data
- API response time is acceptable

---

## Required Tables

Optional for snapshots:

```text
metric_snapshots
```

---

## Required Integrations

- Prometheus
- Spring Boot Actuator

---

# Incident Management

## Verify:
- incidents are auto-created
- severity levels work
- incident lifecycle works
- comments/history persist correctly

---

## Required Tables

```text
incidents
incident_events
incident_comments
incident_assignments
```

---

# Alerting System

## Verify:
- alert rules trigger correctly
- thresholds work properly
- duplicate alerts are prevented
- resolved alerts update correctly

---

## Required Tables

```text
alert_rules
alerts
```

---

# AI Analysis

## Verify:
- AI summaries use real data
- AI does not hallucinate infrastructure state
- AI output is sanitized
- AI responses do not expose secrets

---

## Required Tables

```text
ai_analysis
```

---

# Notifications

## Verify:
- notifications are delivered correctly
- retry logic exists
- failed notifications are tracked

---

## Required Tables

```text
notifications
notification_channels
```

---

# Audit Logging

## Verify:
- important actions are logged
- sensitive data is excluded
- audit history is immutable

---

## Required Tables

```text
audit_logs
```

---

# API Verification Rules

All APIs must verify:

---

## REST Standards

Use:
- proper HTTP methods
- proper status codes
- DTO-based responses

Examples:

```text
GET    /api/projects
POST   /api/incidents
PUT    /api/alerts/{id}
DELETE /api/services/{id}
```

---

## API Security

Verify:
- JWT validation
- role authorization
- organization ownership
- input validation

---

## Error Handling

Verify:
- consistent error structure
- no stack traces exposed
- no sensitive details leaked

Example:

```json
{
  "message": "Unauthorized",
  "status": 401,
  "timestamp": "2026-05-18T12:00:00"
}
```

---

# Frontend Verification Rules

---

## Angular Standards

Verify:
- no hardcoded backend URLs
- typed interfaces exist
- loading states exist
- error states exist
- route guards work

---

## Dashboard Standards

Verify:
- charts update correctly
- real-time refresh works
- data formatting is correct
- large datasets are handled safely

---

# Security Verification Checklist

The agent must verify:

- no secrets committed
- passwords hashed
- JWT secrets externalized
- CORS configured safely
- SQL injection prevented
- XSS prevented
- DTO validation exists
- rate limiting considered
- admin endpoints protected

---

# Docker Verification Rules

Verify:
- containers build successfully
- services communicate correctly
- environment variables work
- health checks exist

---

# Performance Verification Rules

The agent should verify:

- no N+1 queries
- pagination exists where needed
- large responses are optimized
- unnecessary polling avoided
- indexes exist on important columns

---

# Logging Verification Rules

Verify:
- structured logging exists
- incidents/errors are logged
- sensitive information is never logged

Never log:
- passwords
- tokens
- API keys
- secrets

---

# Production Readiness Checklist

Before marking a feature complete:

## Backend
- builds successfully
- tests pass
- endpoints verified
- security verified

## Frontend
- compiles successfully
- responsive UI works
- API integration verified

## Database
- schema validated
- migrations safe
- relationships verified

## Infrastructure
- Docker verified
- environment configs verified
- observability working

---

# Agent Behavior Requirements

The AI coding agent must:

- prefer maintainability over shortcuts
- avoid unnecessary refactors
- explain architectural decisions
- identify risks before implementation
- preserve backward compatibility
- follow clean architecture principles

The agent must think like a production engineer building enterprise SaaS software.

---

# Long-Term Architecture Goal

CloudPulse should evolve into:

- enterprise observability platform
- incident management system
- AI-assisted operations center
- cloud-native monitoring platform

The architecture should remain:
- modular
- secure
- scalable
- testable
- maintainable
- deployment-ready
