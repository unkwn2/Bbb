# Steering Wheel Button Remapping Architecture

This document outlines the architecture and mechanism used to remap the steering wheel buttons on BYD vehicles within the Open BYD Assistant. 

## Background
The proprietary BYD vehicle operating system allows certain privileged apps (like the system dashcam, voice assistant, and HVAC controls) to respond to hardware button presses. However, third-party apps usually cannot intercept these globally.

By analyzing community tools (like DiPlus), we found that the most reliable method to intercept and suppress these button events across the entire Android system is to utilize an **Accessibility Service** equipped with the `FLAG_REQUEST_FILTER_KEY_EVENTS` capability.

## Mechanism

### 1. Accessibility Service & Key Filtering
Android provides Accessibility Services with the ability to observe and intercept user interactions, including hardware key events, before they are dispatched to the currently focused window or application.

In Open BYD, we implement a `SteeringWheelAccessibilityService`. In its configuration, we set `android:canRequestFilterKeyEvents="true"`.

When a button on the steering wheel is pressed, the event is routed to the `onKeyEvent(KeyEvent event)` method of our service.

### 2. Event Suppression
The `onKeyEvent` method returns a `boolean` value:
- **`true` (Consume):** Tells the Android system that our service has fully handled the event. The OS drops the event, and the original BYD system (e.g., the default voice assistant or climate control app) never receives it. This effectively *suppresses* the original behavior.
- **`false` (Pass-through):** Tells the Android system that we didn't handle the event, allowing it to propagate normally to the vehicle's default apps.

Our service checks the incoming `keyCode` against a user-defined mapping database. If a custom action is defined for that keycode, we execute the action and return `true`. Otherwise, we return `false`.

### 3. Keycodes
Based on DiPlus configuration (`strings.xml`), the relevant BYD steering wheel keycodes include:
- `87`: Next Track
- `88`: Previous Track
- `289`: Mode Button
- `291`: Volume Up
- `292`: Volume Down
- `293`: Mute
- `294`: Surround View (Camera)
- `302`: Next (Long Press)
- `303`: Previous (Long Press)
- `304`: Voice Assistant
- `305`: Rotate Screen
- `306`: Rotate Screen (Long Press)
- `312`: Voice Assistant (Long Press)
- `313`: Phone / Call
- `317`: Power Button

### 4. Auto-Enabling the Service
Standard Android apps require the user to navigate deep into `Settings > Accessibility` to manually enable an Accessibility Service. However, Open BYD Assistant uses an ADB-initiated privilege escalation proxy (`UID 2000` / shell). 

This proxy has the `WRITE_SECURE_SETTINGS` permission, which allows us to programmatically write to the system settings database. We automatically enable the `SteeringWheelAccessibilityService` by appending its component name to the `enabled_accessibility_services` secure setting.

```kotlin
// Example shell command executed by the proxy to auto-enable
settings put secure enabled_accessibility_services com.sr.openbyd/com.sr.openbyd.services.SteeringWheelAccessibilityService
settings put secure accessibility_enabled 1
```

## Custom Action Mapping
The user can map these buttons to execute various functions:
1. **App Functions:** Execute a specific Open BYD feature (e.g., Set AC to 22°C, roll down driver window).
2. **Launch Application:** Launch a third-party app by its package name.

The mappings are stored locally and read dynamically by the service.
