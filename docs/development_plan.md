# Open BYD Assistant - Master Development Plan

This document tracks the end-to-end development progress for the Open BYD Assistant application. 
Use the checkboxes to mark tasks as completed during development sessions.

## Phase 1: Core IPC & Privilege Proxy (Foundation)
- [x] Identify correct framework JARs for DiLink 5.1 (`services.jar`, `dilink-services.jar`).
- [x] Fix and test `launch_proxy.sh` injection script.
- [x] Verify successful execution of `app_process` and proxy start via ADB.
- [x] Create a `BroadcastReceiver` in the main UI app to receive the `ProxyBinderParcelable` intent (`com.sr.openbyd.assistant.PROXY_CONNECTED`).
- [x] Define basic test methods in `ICarControl.aidl` (e.g., a simple `ping()` method).
- [x] Implement the `ping()` method in `CarControlImpl.kt`.
- [x] Verify two-way Binder IPC communication between the unprivileged App and the privileged Proxy.

## Phase 2: Local ADB Automation
- [x] Complete the `AdbConnectionManager` initialization logic.
- [x] Implement the ADB client protocol to connect to `127.0.0.1:5555` programmatically.
- [x] Automate sending the `app_process` shell command from the app's startup sequence.
- [x] Implement logic to verify if the proxy is already running before attempting to start it again.
- [x] Ensure the proxy starts silently and reliably without needing a laptop/external PC.

## Phase 3: BYD API Integration (Car Control)
- [ ] Map the necessary hidden `android.hardware.bydauto.*` classes (e.g., `BYDAutoBodyworkDevice`, `BYDAutoAcDevice`, `BYDAutoPowerDevice`).
- [ ] Create stub classes or reflection wrappers in the `:proxy-server` module to compile against these APIs.
- [ ] Update `ICarControl.aidl` with specific control methods:
    - [x] Windows (Up/Down/Vent)
    - [x] AC (On/Off/Temp control)
- [ ] Implement these control methods inside `CarControlImpl.kt`, making the actual BYD hardware calls.
- [ ] Test hardware triggers manually via simple UI buttons.

## Phase 4: Voice Assistant Integration
- [ ] Evaluate and select an offline voice recognition engine (e.g., Vosk, PocketSphinx).
- [ ] Integrate the voice engine into the main app module.
- [ ] Create a wake-word detection service.
- [ ] Define a JSON mapping or intent system for translating phrases into vehicle actions.
- [ ] Tie recognized voice commands to the `ICarControl` proxy calls.

## Phase 5: Steering Wheel Button Remapping
- [ ] Identify how DiLink broadcasts steering wheel button events (likely via standard Android KeyEvents or a specific BYD broadcast).
- [ ] Implement an `AccessibilityService` or `BroadcastReceiver` to intercept these hardware button presses.
- [ ] Prevent the default system action from occurring if a button is mapped (if possible).
- [ ] Create a Compose UI screen allowing the user to map specific buttons (e.g., "Voice Assistant" button) to custom actions or to launch third-party apps (like Spotify/Waze).

## Phase 6: Polish, UI, & Release
- [ ] Build a complete, responsive Jetpack Compose dashboard UI.
- [ ] Add multilingual support (English, Spanish, etc.) for both the UI and the Voice Assistant models.
- [ ] Implement crash recovery: If the proxy dies, the app should automatically restart it via the internal ADB client.
- [ ] Code cleanup, documentation, and open-source release preparation.
