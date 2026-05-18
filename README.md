# Gemini-Parking

> **Parking Assistance for Residential Compounds**
> *A Gemma 4 Good Hackathon Submission*

> **Note:** The project is named *Gemini-Parking* after the Gemini API used for AI-powered license plate recognition. It is submitted to the *Gemma 4 Good Hackathon*. The two names refer to different Google AI products — the project name reflects the specific API integrated into the system.

Gemini-Parking is a lightweight parking assistance system designed for residential compounds and gated communities. It helps security guards identify a vehicle from a license plate photo and instantly retrieve the associated resident's phone number, building, and room — replacing slow manual lookups with a fast, AI-powered workflow.

The project also provides a web-based management system for property staff to register and maintain vehicle-resident records.

---

## Problem

In residential compounds, security guards regularly deal with:

- Unknown or improperly parked vehicles blocking access
- The need to contact vehicle owners quickly, especially during peak hours or night shifts
- Resident and vehicle records stored in paper logs, spreadsheets, or messaging groups
- No standard system to look up who owns a specific vehicle

Finding the right resident is often slow, error-prone, and inefficient.

---

## Solution

Gemini-Parking provides a simple, end-to-end workflow:

1. A security guard captures or uploads a license plate photo
2. The system uses AI-powered plate recognition to extract the plate number
3. The backend searches the resident-vehicle directory
4. The resident's **phone number**, **building**, and **room** are returned immediately
5. Property staff can maintain the directory through a web dashboard

---

## Target Users

| User | Interface | Main Task |
|------|-----------|-----------|
| Security guard (on-site) | Mobile App | Capture plate, view resident info, contact owner |
| Property staff / guard office | Web Dashboard | Register vehicles, manage resident records |

---

## Core Features

- **License Plate Recognition** — upload or photograph a plate and extract the number automatically
- **Resident Lookup** — match the plate number against the resident-vehicle directory
- **Contact Retrieval** — return phone number, building, and room in one step
- **Record Management** — add, edit, and maintain vehicle-resident mappings via the web dashboard
- **Operation Logging** — optionally record all lookup and registration actions for auditing

---

## Product Structure

```text
/app        Mobile interface — on-site plate lookup for security guards
/web        Web dashboard — record management for property staff
/backend    API services and AI-powered recognition logic
/sql        Database schema, initialization scripts, and seed data
/data       Sample plate images, mock resident records, and demo files
```

### `/app` — Mobile App
Designed for front-line security guards at the gate or parking area.

Key capabilities:
- Capture or upload a license plate image
- View matched resident information (phone, building, room)
- One-tap contact to call the resident

### `/web` — Web Dashboard
Designed for property management staff or the guard office on a desktop.

Key capabilities:
- Register new vehicle-resident records
- Search and edit the parking directory
- View lookup and registration history

### `/backend` — Backend Services
Handles all server-side logic.

Key responsibilities:
- Accept image upload requests
- Run AI-powered plate recognition to extract plate numbers
- Query and update the resident-vehicle database
- Expose a RESTful API to both `/app` and `/web`

Example API endpoints:
- `POST /recognize-plate` — upload a plate image and return the recognized number
- `GET /vehicles/:plateNumber` — retrieve resident info by plate
- `POST /records` — create a new vehicle-resident record
- `PUT /records/:id` — update an existing record
- `GET /records` — list all registered records

### `/sql` — Database Scripts
Contains all database artifacts.

- Table creation scripts
- Index definitions
- Seed / demo data

### `/data` — Sample Data & Demo Assets
Contains resources for testing and demonstration.

- Sample plate images (`sample-plates/`)
- Mock resident records (`mock-residents.csv`)
- Seed data files (`seed.json`)

---

## Example User Flows

### Flow 1 — Security Guard Looks Up a Vehicle

```
1. Guard opens the mobile app
2. Captures a photo of the license plate
3. App sends the image to the backend
4. Backend recognizes the plate number
5. Backend queries the database
6. App displays: phone number, building, room
7. Guard contacts the resident directly
```

### Flow 2 — Staff Registers a New Record

```
1. Staff opens the web dashboard
2. Enters the plate number (or uploads an image)
3. Fills in: phone number, building, room
4. Saves the record
5. Record is immediately searchable by guards
```

---

## High-Level Architecture

```
License Plate Photo (App / Web)
             │
             ▼
     Backend API Service
             │
             ▼
    AI-Powered Plate Recognition
             │
             ▼
  Plate Number Extracted
             │
             ▼
  Resident Database Lookup
             │
             ▼
  Phone Number + Building + Room
        returned to client
```

---

## Suggested Data Model

### `vehicles` table
| Field | Type | Notes |
|-------|------|-------|
| `id` | integer | Primary key |
| `plate_number` | varchar | Unique |
| `phone` | varchar | Owner contact |
| `building` | varchar | e.g. Block A |
| `room` | varchar | e.g. 12B |
| `plate_image_url` | varchar | Optional |
| `created_at` | timestamp | |

### `access_logs` table
| Field | Type | Notes |
|-------|------|-------|
| `id` | integer | Primary key |
| `plate_number` | varchar | Queried plate |
| `operator` | varchar | Guard / staff ID |
| `result_status` | varchar | Found / not found |
| `queried_at` | timestamp | |

---

## Why This Is a Good Hackathon Project

- **Real-world impact**: solves a daily operational pain point in residential compounds
- **AI at the core**: license plate recognition is the essential value driver
- **Clearly scoped MVP**: end-to-end flow can be demonstrated in minutes
- **Dual-interface design**: shows both field use (mobile) and management use (web)
- **Extensible foundation**: easily grows into visitor management, alerts, or gate integration

---

## Future Roadmap

- Improve plate recognition accuracy under low-light conditions
- Support multiple vehicles per resident
- Add visitor vehicle pre-registration
- Add alerts for unrecognized or flagged vehicles
- Integrate with physical access control or gate systems
- Add role-based permissions and full audit logging

---

## Current Status

Hackathon MVP / Prototype

---

## Demo Value

- Show a guard capturing a plate photo and receiving resident info in seconds
- Show property staff adding a new record via the web dashboard
- Demonstrate the full end-to-end flow: image → recognition → lookup → contact

---

## Project Goal

Gemini-Parking aims to make parking-related communication in residential compounds faster, simpler, and more reliable. By combining AI-powered license plate recognition with a lightweight resident directory, the system helps security guards reach the right person in seconds — reducing friction, improving response times, and making day-to-day compound management more efficient.

---

## License

This project is licensed under the [Apache License 2.0](LICENSE).
