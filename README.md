# Online Voting System

A small Swing-based Java application demonstrating a simple voting system.

## Features
- Single source file: `OnlineVotingSystem.java`.
- Persistent voter storage in `voters.txt` (format: `name|father|dob|cnic|voted`).
- Names/father fields are stored in lowercase; lookups are case-insensitive.
- Admin panel with live table updates when adding/deleting/voting.
- Splash screen on start and single-instance windows (reuses open windows).

## Requirements
- Java 8 or newer installed and on the PATH.

## Files
- `OnlineVotingSystem.java` — main source.
- `voters.txt` — data file used for persistence (created/updated by the app).

## Build & Run
Compile:

```bash
javac "c:\Users\ESHOP\Desktop\Online Votting System\OnlineVotingSystem.java"
```

Run:

```bash
java -cp "c:\Users\ESHOP\Desktop\Online Votting System" OnlineVotingSystem
```

## Data file format
Each line represents one voter with fields separated by `|`:

```
name|father|dob|cnic|voted
```

- `voted` is `0` (not voted) or `1` (voted).
- Example: `ali|ahmed|01-01-2000|42101|0`

## Notes
- The app creates `voters.txt` if it doesn't exist and saves changes automatically when voters are added, deleted, or when a vote is cast.
- Names and father fields are converted to lowercase on save to allow case-insensitive verification.

## Next steps (ideas)
- Add search/filter in the admin panel.
- Improve visuals (icons, responsive layout, dark mode).
- Migrate persistence to a lightweight embedded DB (e.g., SQLite) for robustness.

If you want any of these, tell me which and I will implement them next.
