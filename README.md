# AoE4 Civ Randomizer

A simple local Java web app for randomizing **Age of Empires 4** civilizations.

- Pick a random civ for yourself with one click.
- Assign random civs to a full lobby of players (with or without duplicates).
- Enable / disable individual civilizations — settings persist across restarts.
- Runs entirely on **localhost** — no internet connection, no account, no install needed.

---

## Prerequisites

| Tool | Version |
|------|---------|
| JDK  | 17 or newer |
| Maven | 3.6+ (or use `./mvnw` if a Maven wrapper is added later) |

---

## How to run in IntelliJ IDEA

1. **Open the project** — File → Open → select the `aoe4-civ-randomizer` folder.
2. **Let Maven import** — IntelliJ will detect `pom.xml` and download dependencies automatically.
3. **Run the application** — open `src/main/java/com/aoe4/randomizer/Aoe4RandomizerApplication.java` and click the green ▶ button next to the `main` method.
4. **Open your browser** at [http://localhost:8080](http://localhost:8080).

---

## How to run from the command line

```bash
mvn spring-boot:run
```

Then open [http://localhost:8080](http://localhost:8080).

---

## How to edit the civilization list

The full civ list lives in one easy-to-edit file:

```
src/main/resources/data.sql
```

Each line follows this pattern:

```sql
INSERT INTO civilization (name, dlc, icon_path, enabled)
SELECT 'My New Civ', 'Base Game', '/images/civs/my-new-civ.png', TRUE
WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'My New Civ');
```

- **Add a civ** — copy any existing `INSERT` block and change the name, DLC group, and icon path.
- **Remove a civ** — delete its block (or just uncheck it in the UI instead).
- **Rename a civ** — change both the `SELECT` value and the `WHERE name =` value.

The `WHERE NOT EXISTS` guard means your enable/disable choices are never overwritten on restart.

---

## Notes

- **Local use only** — no authentication, no multi-user support, no network exposure.
- **H2 database** — stored in `./data/aoe4randomizer.mv.db` (gitignored). Delete this file to reset all settings to defaults.
- **H2 console** (optional debugging) — uncomment the two lines in `application.properties` to enable it at `http://localhost:8080/h2-console`.
- **Civ icons** — local files are stored in `src/main/resources/static/images/civs/`. Keep filenames lowercase and hyphenated to match `icon_path` values.
- **Planned features** (not in this version): pick history / stats, multi-profile support.
