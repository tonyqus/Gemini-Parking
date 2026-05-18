# Demo Script — Gemini-Parking

## Demo Title

**Gemini-Parking: From Plate Photo to Resident Contact in Seconds**

---

## Demo Goal

Show judges a complete, working end-to-end flow: a security guard photographs a license plate, the system recognizes it, and the guard instantly has the resident's contact details. Then show how property staff keep the directory up to date.

---

## Recommended Demo Length

**3 to 5 minutes**

---

## Demo Flow Overview

| Section | Duration | Content |
|---------|----------|---------|
| 1. Opening / Problem | ~30 s | Frame the problem clearly |
| 2. Guard Workflow (App) | ~60 s | Take photo → get result |
| 3. Recognition Result | ~30 s | Show returned data |
| 4. Web Dashboard | ~60 s | Register a new record |
| 5. System Structure | ~30 s | Quick architecture overview |
| 6. Closing / Impact | ~30 s | Why it matters, what comes next |

---

## Full Demo Script

---

### Section 1 — Opening / Problem (~30 seconds)

> "Imagine you're a security guard at a residential compound.
> A car is blocking the entrance. You don't know who it belongs to.
> Your options: search a paper log, ask colleagues, or wait.
>
> This happens every day in compounds around the world.
> It's slow, manual, and frustrating for everyone involved.
>
> We built **Gemini-Parking** to fix that."

---

### Section 2 — Guard Workflow (Mobile App, ~60 seconds)

> "Here's our mobile app, designed for security guards on-site.
>
> The guard sees an unknown vehicle. They open the app and tap the camera button.
> They point the phone at the license plate — like this — and take the photo.
>
> The app sends the image to our backend.
> The backend runs AI-powered plate recognition.
> The plate number is extracted automatically.
> The system looks it up in the resident directory."

*[Show the app taking the photo and the loading indicator briefly appearing.]*

---

### Section 3 — Recognition Result (~30 seconds)

> "And here's the result — returned in under three seconds.
>
> Phone number: +60 12-345 6789.
> Building: Block A.
> Room: 12B.
>
> The guard taps the phone number to call the resident directly.
> Problem solved. No paper. No searching. No waiting."

*[Show the result screen clearly. Optionally tap the call button.]*

---

### Section 4 — Web Dashboard (~60 seconds)

> "Now let's look at the other side of the system — the web dashboard.
>
> This is used by property staff or the guard office to manage the resident-vehicle directory.
>
> Here they can add a new record: enter the plate number, fill in the phone, building, and room — or upload a plate image to auto-fill the number.
>
> Save. Done. That record is now immediately searchable by any guard using the app.
>
> They can also see the history of recent lookups — which plates were queried, when, and by whom."

*[Show the registration form being filled in and saved. Optionally show the records list.]*

---

### Section 5 — System Structure (~30 seconds)

> "Under the hood, the system is clean and modular.
>
> `/app` is the mobile interface.
> `/web` is the management dashboard.
> `/backend` handles the API, recognition, and database queries.
> `/sql` contains our schema and seed scripts.
> `/data` has our demo images and mock records.
>
> The recognition pipeline is: image in, plate number out, resident info returned."

*[Optionally show the architecture diagram or repository structure briefly.]*

---

### Section 6 — Closing / Impact (~30 seconds)

> "Gemini-Parking is small by design — but it solves a real daily problem for millions of residential compounds.
>
> The core AI workflow — photo to identity in seconds — can extend to visitor registration, vehicle alerts, and gate integration.
>
> We built this to show that AI doesn't have to be complex to be useful.
> Sometimes the most valuable system is the one a security guard can use with one tap.
>
> Thank you."

---

## Optional 60-Second Version

Use this if time is very limited or for a lightning round:

> "Security guards in residential compounds waste time every day looking up who owns an unknown vehicle.
> Gemini-Parking solves this.
>
> The guard takes a photo of the plate.
> Our AI extracts the number.
> The system returns the resident's phone, building, and room — in seconds.
>
> Property staff maintain the directory through a simple web dashboard.
>
> The system is modular, open-source, and ready to extend.
> From plate photo to resident contact. That's Gemini-Parking."

---

## Recommended Demo Assets

| Asset | Purpose |
|-------|---------|
| Sample license plate images | Realistic input for the recognition demo |
| Pre-loaded mock resident records | Show instant lookup results |
| Mobile app on a real device or emulator | Live demonstration of the guard workflow |
| Web dashboard on a laptop or second screen | Show the management side |
| Architecture diagram (one slide) | Quick visual for the system structure section |

---

## Suggested Final Line

> "Gemini-Parking: helping security guards reach the right resident, every time, in seconds."
