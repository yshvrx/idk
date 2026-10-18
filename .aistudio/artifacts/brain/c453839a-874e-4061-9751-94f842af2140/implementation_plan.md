# Minimal Productivity App — Tasks, Calendar & Notes

A peaceful, minimalist Android productivity suite combining the clean task execution of Apple Reminders, the structured schedule clarity of Notion Calendar, and the distraction-free writing experience of Apple Notes—anchored by an interactive retro pixel smiley companion.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during clarification and shape the system design:

- **Visual Theme Direction**: Monochrome Apple aesthetic—crisp pure white surfaces (`#FFFFFF`) in light mode and deep graphite (`#121212`) in dark mode, accented by charcoal typography, hairline borders (`1.dp`), and refined spacing without colorful distractions.
- **Pixelated Smiley Placement**: Dedicated interactive widget card nestled within the dashboard overview. It renders an authentic 8-bit retro face on a canvas grid, tracking cursor or touch coordinates, blinking organically, and celebrating completed tasks with bouncy happy animations.
- **Starter Content**: Seeded with curated starter items on first launch (welcome task checklist, "Getting Started" note with formatting examples, and upcoming agenda events) so the app feels alive immediately.
- **Copyright Attribution**: Prominently yet discreetly includes the exact required notice: `© 2026 Yash Verma. All rights reserved.` in a low-contrast footer.

---

## 1. Overview & Core Concept

- **What It Does**: Provides an all-in-one local-first productivity workspace containing Tasks (reminders, priorities, subtasks, filters), Calendar (monthly/weekly/daily schedules, event editor, date-linked tasks), and Notes (auto-saving, pinned notes, rich formatting, instant search).
- **Target Audience / Persona**: Knowledge workers, students, and minimalists who desire a distraction-free daily cockpit with zero login friction, instant response times, and local privacy.
- **Key Value**: Frictionless task capture, clear time management, and fluid thought drafting united under a cohesive, elegant monochrome design.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **First-Launch Onboarding ("What should we call you?")**:
   - Clean, centered minimal prompt with subtle typography: *"What should we call you?"*
   - Single autofocus text input field with an underline or rounded border and a sleek *"Continue"* button.
   - Saves name locally to preferences and transitions seamlessly to the Dashboard without requiring passwords or emails.

2. **Main Dashboard**:
   - Dynamic time-of-day greeting (e.g., *"Good morning, Yash"*, *"Good afternoon..."*, *"Good evening..."*, *"Welcome back..."*) with the localized date formatted elegantly underneath.
   - **Interactive Pixel Smiley Widget**: High-fidelity retro 8-bit canvas character that monitors cursor hover / touch drag gestures to shift its eye pupils towards the user's touch, blinks at random intervals, and reacts with joyous bouncing when tasks are checked off.
   - **Today's Tasks Preview**: Clean checkboxes with fast one-tap completion and inline quick-add field.
   - **Mini Calendar / Agenda Strip**: Compact week strip highlighting today's scheduled items.
   - **Recent Notes Carousel / List**: Quick access cards showing recently modified notes.

3. **Tasks Screen (Apple Reminders Inspiration)**:
   - Category smart views: **Today**, **Upcoming**, **Completed**, and **All**.
   - Custom Lists / Tag chips (Personal, Work, Ideas).
   - Fast-Add Bar: Type a task title and tap enter or "+", immediately adding it to the list.
   - Rich Task Detail Sheet: Subtasks checklist, Due Date & Time picker, Priority selector (Low, Medium, High with subtle indicators), and Notes description.
   - Micro-interaction: Check circle strike-through animation with subtle haptic/visual delight and smiley reaction.

4. **Calendar Screen (Notion Calendar Inspiration)**:
   - Multi-mode view switcher: Month Grid, Week Column View, and Day Agenda.
   - Clear *"Today"* jump button and month navigation arrows.
   - Merged timeline: Displays calendar events alongside scheduled tasks due on that day.
   - Event creation and editing modal: Title, date/time range, location/notes, and recurring intervals.

5. **Notes Screen (Apple Notes Inspiration)**:
   - Split / Master-Detail responsive layout for tablets and smooth navigation on phones.
   - Pinned notes section on top, followed by chronological last-edited list.
   - Instant search bar filtering titles and body content.
   - Distraction-free writing canvas with live auto-save on typing.
   - Markdown & formatting support: Headers, bullet points, checklists, and body text.
   - Soft trash / archive management.

6. **Settings & Data Management**:
   - Edit user name anytime.
   - Theme toggle (Light / Dark / System).
   - Local backup: One-click JSON Export & Import to guarantee user data sovereignty.
   - Low-contrast copyright footer: `© 2026 Yash Verma. All rights reserved.`

### Visual Identity & Theme Tokens

| Token | Light Mode Value | Dark Mode Value | Semantic Role |
| :--- | :--- | :--- | :--- |
| `surface` | `#FFFFFF` | `#121212` | Main card and canvas surfaces |
| `background` | `#FAFAFA` | `#0A0A0A` | App backdrop |
| `onSurface` | `#111111` | `#F5F5F7` | Primary typography & high contrast icons |
| `onSurfaceVariant` | `#6E6E73` | `#86868B` | Secondary text, timestamps, subtle headers |
| `outline` | `#E5E5EA` | `#2C2C2E` | Thin 1dp dividers and card outlines |
| `primary` | `#000000` | `#FFFFFF` | Primary actions and active pills |
| `accentSuccess` | `#34C759` | `#30D158` | Task completion indicator |
| `accentPriority` | `#FF453A` | `#FF453A` | High priority subtle dot |

---

## 3. Key Product Decisions & Trade-Offs

- **Local Storage via Android Room + SharedPreferences**:
  - *Chosen Approach*: Room SQLite database with DAOs for Tasks, Calendar Events, and Notes; SharedPreferences for User Preferences (name, onboarded flag, theme).
  - *Why*: User explicitly requested persistent local-only storage without authentication or cloud dependencies. Room provides full compile-time SQL verification, reactive `Flow` emissions, fast query sorting, and easy JSON export/import.
  - *Alternatives Considered*: In-memory only (loses data on restart) or cloud database (violates user's explicit no-auth, local-only requirement).

- **Retro Pixelated Smiley Engine (Custom Canvas)**:
  - *Chosen Approach*: A custom Jetpack Compose `Canvas` component drawing on an 8x8 or 10x10 pixel grid. Pupil offsets are mathematically calculated from the local pointer input position (`Modifier.pointerInput`).
  - *Why*: High visual fidelity, true 8-bit charm, zero external asset dependencies, smooth 60fps render loop with `Animatable`, and full accessibility compliance.

- **Responsive Adaptive Layout**:
  - *Chosen Approach*: Mobile bottom navigation bar + Tablet / Landscape Navigation Rail. Large screens leverage dual-pane list-detail for Notes and wide grid layouts for the Calendar.

---

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│  (Edge-to-Edge, Dark/Light Theme Provider, Responsive Window Insets)    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           RootAppNavigation                            │
│  ┌────────────────────────┐  ┌──────────────────────────────────────┐  │
│  │     WelcomeScreen      │  │            MainScaffold              │  │
│  │ (First-launch name)    │  │  ┌───────────┐  ┌──────────────────┐ │  │
│  └────────────────────────┘  │  │ BottomBar │  │ NavigationRail   │ │  │
│                              │  │ (Mobile)  │  │ (Tablet/Desktop) │ │  │
│                              │  └─────┬─────┘  └────────┬─────────┘ │  │
│                              └────────┼─────────────────┼───────────┘  │
└───────────────────────────────────────┼─────────────────┼──────────────┘
                                        ▼                 ▼
 ┌──────────────────────────────────────────────────────────────────────┐
 │                            Active Screens                            │
 │  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────┐ │
 │  │ DashboardView │ │   TasksView   │ │ CalendarView  │ │ NotesView │ │
 │  │ (Smiley Card, │ │ (Reminders,   │ │ (Month/Week/  │ │ (Editor,  │ │
 │  │  Greeting)    │ │  Priorities)  │ │  Day, Agenda) │ │  Search)  │ │
 │  └───────┬───────┘ └───────┬───────┘ └───────┬───────┘ └─────┬─────┘ │
 └──────────┼─────────────────┼─────────────────┼───────────────┼───────┘
            │                 │                 │               │
            ▼                 ▼                 ▼               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                  AppViewModel (Single Source of Truth)               │
│  - StateFlow<DashboardUiState>    - StateFlow<TasksUiState>          │
│  - StateFlow<CalendarUiState>     - StateFlow<NotesUiState>          │
│  - StateFlow<UserPreferences>     - Task completion celebration bus  │
└───────────────────────────────────┬──────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           Data Layer                                   │
│  ┌────────────────────────┐           ┌──────────────────────────────┐ │
│  │     Room Database      │           │     PreferencesRepository    │ │
│  │  - TaskDao             │           │  - User Name                 │ │
│  │  - EventDao            │           │  - Onboarding Completed      │ │
│  │  - NoteDao             │           │  - Theme Setting             │ │
│  └────────────────────────┘           └──────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

### Data Entities & Schema

1. **`TaskEntity`**:
   - `id: Long` (Primary Key, autoincrement)
   - `title: String`, `description: String`, `isCompleted: Boolean`
   - `dueDateMillis: Long?`, `priority: String` ("LOW", "MEDIUM", "HIGH")
   - `category: String` ("Personal", "Work", "General")
   - `subtasksJson: String` (serialized list of subtasks `{id, text, done}`)
   - `createdAt: Long`, `completedAt: Long?`

2. **`EventEntity`**:
   - `id: Long` (Primary Key, autoincrement)
   - `title: String`, `description: String`
   - `startEpochMillis: Long`, `endEpochMillis: Long`
   - `isAllDay: Boolean`, `colorTag: String`

3. **`NoteEntity`**:
   - `id: Long` (Primary Key, autoincrement)
   - `title: String`, `content: String`
   - `isPinned: Boolean`, `isArchived: Boolean`
   - `updatedAt: Long`, `createdAt: Long`

4. **`UserPreferences`**:
   - `userName: String`
   - `isOnboarded: Boolean`
   - `isDarkMode: Boolean?` (null = system default)

### Implementation Steps

1. **Metadata & Resources**:
   - Update `metadata.json` with `name = "Minimal Productivity"` and matching `res/values/strings.xml`.
2. **Data & Storage Layer**:
   - Implement Room entities, DAOs, and `AppDatabase`.
   - Implement `PreferencesManager` for persistent user identity and app settings.
   - Implement pre-population of starter tasks, sample calendar events, and welcoming Apple-style note.
3. **Core ViewModel & Business Logic**:
   - Manage reactive state streams, filtering (Today, Upcoming, Completed, All), task completion reactions, and JSON export/import.
4. **Interactive Pixelated Smiley Component**:
   - Compose canvas grid with eye-tracking physics, organic blinking coroutine, and celebratory bounce animations.
5. **Screens & Navigation**:
   - **WelcomeScreen**: Clean typography, autofocus input, seamless continue action.
   - **DashboardScreen**: Greeting, date, smiley card, quick task entry, today's focus, upcoming agenda, recent notes.
   - **TasksScreen**: Apple Reminders aesthetic, custom filter chips, inline fast-add, task edit modal.
   - **CalendarScreen**: Notion-style clean month/week grid, date picker, today button, event modal.
   - **NotesScreen**: Apple Notes style, list/editor split, live search, pin toggles, markdown checklists.
   - **SettingsScreen**: Name change, backup export/import, discrete copyright footer: `© 2026 Yash Verma. All rights reserved.`
6. **Polishing & Verification**:
   - Edge-to-edge support, accessible touch targets, and compilation check with `compile_applet`.
