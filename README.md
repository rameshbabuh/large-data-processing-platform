# Large Data Processing Platform

A full-stack cloud-based application for uploading and asynchronously processing large CSV datasets.

## Planned Tech Stack

### Frontend
- Angular
- Angular Material
- RxJS

### Backend
- Java 21
- Spring Boot
- Spring Batch
- PostgreSQL

### Cloud
- Google Cloud Run
- Google Cloud Storage
- Pub/Sub
- Cloud SQL

## Goals

The application will support:

- Multi-GB CSV uploads
- Asynchronous dataset processing
- Chunk-based processing
- Job progress tracking
- Row-level validation
- Error reporting
- Retry and failure recovery
- Cloud deployment on GCP

## Project Structure

```text
backend/
frontend/
sample-data/