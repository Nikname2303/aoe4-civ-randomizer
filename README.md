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

## Desktop mode (JavaFX wrapper)

Desktop mode runs the same Spring Boot app locally and opens it in a native window.

```bash
mvn -Pdesktop javafx:run
```

Or from a built jar:

```bash
java -jar target/randomizer-0.0.1-SNAPSHOT.jar --desktop
```

- Default desktop port: `18080` (keeps normal web mode on `8080` unchanged).
- If needed, override desktop port:

```bash
mvn -Pdesktop -Ddesktop.port=19090 javafx:run
```

---

## Build a Windows desktop package (jpackage)

### Prerequisites

- JDK 17+ that includes `jpackage` (for example Temurin/OpenJDK full JDK install)
- Maven 3.6+
- Windows machine for building Windows `.exe` / `.msi`

### 1) Build the app jar

```bash
mvn clean package
```

### 2) Build the desktop launcher package

Run this from the project root (PowerShell example):

```powershell
jpackage `
  --input target `
  --name "AoE4 Civ Randomizer" `
  --main-jar randomizer-0.0.1-SNAPSHOT.jar `
  --main-class org.springframework.boot.loader.launch.JarLauncher `
  --type exe `
  --arguments "--desktop" `
  --java-options "-Ddesktop.port=18080 -Dcom.sun.webkit.useHTTP2Loader=false"
```

Notes:
- Use `--type msi` if you want an MSI installer.
- Use `--type app-image` to produce a self-contained folder (no installer) that can be zipped and shared directly — the folder contains the `.exe` and the bundled JVM runtime, so recipients can run it without any installation step.
- Optional icon: add `--icon path\to\icon.ico`. If omitted, default icon is used.

---

## Troubleshooting

- **Civ icons appear as generic placeholders in desktop mode**  
  This is a known JavaFX WebView HTTP/2 loader bug. It is already worked around in code
  (`DesktopLauncherSupport.disableWebViewHttp2Loader()` is called at startup). If it
  recurs (e.g. when running directly via IDE without the desktop profile), pass
  `-Dcom.sun.webkit.useHTTP2Loader=false` as a java-option.

- **Port conflict in desktop mode**  
  If startup says desktop port is in use, close the conflicting app or run with another port:
  `-Ddesktop.port=19090`.

- **JavaFX class/module errors**  
  Make sure you used the desktop profile command: `mvn -Pdesktop javafx:run`.

- **`jpackage` not found**  
  Install a full JDK (not JRE), then verify:
  `jpackage --version`.

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
- **H2 database** — stored in `~/.aoe4-civ-randomizer/data/aoe4randomizer.mv.db` (gitignored). Delete this file to reset all settings to defaults.
- **H2 console** (optional debugging) — uncomment the two lines in `application.properties` to enable it at `http://localhost:8080/h2-console`.
- **Civ icons** — local files are stored in `src/main/resources/static/images/civs/`. Keep filenames lowercase and hyphenated to match `icon_path` values.
- **Planned features** (not in this version): pick history / stats, multi-profile support.
