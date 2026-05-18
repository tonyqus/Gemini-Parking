# Hackathon Submission — Gemini-Parking

## Project Name

**Gemini-Parking**

---

## Tagline

*From plate photo to resident contact in seconds.*

---

## Short Description

Gemini-Parking is a lightweight parking assistance system for residential compounds. It lets security guards photograph a license plate and instantly retrieve the vehicle owner's phone number, building, and room. A web dashboard allows property staff to maintain the vehicle-resident directory. The system replaces slow manual lookups with a fast, AI-powered workflow.

---

## Problem

Security guards in residential compounds frequently need to locate vehicle owners — to resolve blocked access, improper parking, or unauthorized vehicles. Today, this process is manual:

- Resident and vehicle records are stored in paper logs, spreadsheets, or informal chat groups
- Guards must search through records by hand or rely on memory
- Response times are slow, especially during peak hours or night shifts
- Records are often incomplete or out of date

The result: a routine task that should take seconds can take minutes, causing unnecessary friction for residents, staff, and visitors.

---

## Solution

Gemini-Parking provides a complete, minimal workflow:

1. Security guard captures or uploads a license plate photo
2. AI-powered plate recognition extracts the plate number
3. The system queries the resident-vehicle directory
4. Phone number, building, and room are returned to the guard immediately
5. Property staff can register and maintain records through a web dashboard

The goal is to reduce the time from "unknown vehicle" to "resident contacted" to under ten seconds.

---

## Target Users

| User | Interface | Core Need |
|------|-----------|-----------|
| Security guard | Mobile App | Identify vehicle owner quickly, on-site |
| Property staff / guard office | Web Dashboard | Register vehicles, manage resident records |

---

## Key Features

- **License plate recognition**: capture or upload a photo and extract the plate number automatically
- **Resident lookup**: match the plate number against the stored directory
- **Contact retrieval**: return phone number, building, and room in one response
- **Record management**: add, edit, and delete vehicle-resident records via the web interface
- **Operation logging**: record lookup and registration activity for auditing and accountability

---

## Product Components

### Mobile App (`/app`)
- Designed for on-site security guards
- Capture or upload a license plate image
- Display matched resident info: phone number, building, and room
- Direct call action to contact the resident

### Web Dashboard (`/web`)
- Designed for property staff or the guard office desktop
- Register and edit vehicle-resident records
- Search the parking directory
- View lookup and registration history

### Backend (`/backend`)
- REST API serving both app and web clients
- Image upload and AI-powered plate recognition pipeline
- Resident-vehicle database queries and CRUD operations

### Database (`/sql`)
- Relational schema: `vehicles`, `owners`, `access_logs`
- Initialization scripts and seed data

### Demo Assets (`/data`)
- Sample plate images
- Mock resident data
- Seed files for local development and demo

---

## Repository Structure

```text
/app        Mobile app — on-site lookup for security guards
/web        Web dashboard — record management for property staff
/backend    API services and recognition logic
/sql        Database schema and initialization scripts
/data       Sample images, mock data, and demo assets
```

---

## User Flow

### Guard Lookup Flow

```
Guard opens app
    → Captures license plate photo
        → App uploads image to backend
            → Backend runs AI-powered plate recognition
                → Plate number extracted
                    → Database queried
                        → Result returned:
                          Phone: +60 12-345 6789
                          Building: Block A
                          Room: 12B
                            → Guard contacts resident
```

### Staff Registration Flow

```
Staff opens web dashboard
    → Enters plate number (or uploads image)
        → Fills in phone, building, room
            → Submits form
                → Record saved to database
                    → Immediately searchable by guards
```

---

## Technical Overview

### Stack (suggested / flexible)

| Layer | Technology |
|-------|------------|
| Mobile App | React Native / Flutter |
| Web Dashboard | React / Vue |
| Backend API | Node.js / Python (FastAPI) |
| Recognition | Gemini API / Google Cloud Vision / Tesseract OCR |
| Database | PostgreSQL / MySQL |

### Processing Flow

```
Input: license plate image
    ↓
Image upload to backend API
    ↓
AI-powered plate recognition processes the image
    ↓
Plate number text extracted
    ↓
Database lookup by plate number
    ↓
Output: phone number + building + room
```

---

## Why This Project Matters

- **Addresses a real operational problem** faced daily in residential compounds worldwide
- **AI is the core value driver**, not an add-on — recognition is what makes the system work
- **Low barrier to adoption** — guards need no training; the workflow is photograph and read
- **Immediate, measurable impact** — response time for parking issues drops significantly
- **Strong extensibility** — visitor registration, alerts, gate integration, and audit logs can follow

---

## Future Roadmap

| Phase | Feature |
|-------|---------|
| Post-MVP | Multiple vehicles per resident |
| Post-MVP | Visitor vehicle pre-registration |
| Growth | Unrecognized vehicle alerts |
| Growth | Integration with physical gate / barrier systems |
| Growth | Role-based access control and audit trails |
| Growth | Multi-compound / multi-site support |

---

## Current Status

**Hackathon MVP / Prototype**

The current submission establishes the system architecture, repository structure, core workflows, and data model. The MVP demonstrates the end-to-end flow from plate image input to resident contact retrieval.

---

## Demo Summary

The demo shows:

1. A security guard opening the mobile app and photographing a license plate
2. The system recognizing the plate and returning the resident's contact details
3. A property staff member opening the web dashboard to add a new record
4. The full pipeline: image → recognition → lookup → contact

---

## Team Notes

- All submission documents are written in English for international reviewers
- The system is designed to be lightweight and deployable in low-resource environments
- The architecture is modular: each component (`/app`, `/web`, `/backend`, `/sql`, `/data`) can be developed and deployed independently
- The project is open-source under the Apache License 2.0
