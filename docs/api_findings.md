# BYD DiLink 5.1 API Architecture Findings

Based on the reverse engineering of `services.jar` and `dilink-services.jar` from the car, here is a breakdown of how the hidden `android.hardware.bydauto` APIs function.

## 1. The Generic Architecture

Instead of having hundreds of specific methods (like `setWindowUp()`, `getAcTemperature()`), BYD consolidates hardware interaction into generic methods on "Device" classes (e.g., `BYDAutoBodyworkDevice`, `BYDAutoAcDevice`). 

All interaction is routed through three primary paradigms:

### A. Listeners (`registerListener`)
* **What it does:** Subscribes to hardware events. When physical hardware changes state (e.g., the user presses the physical AC button, or the car's power level changes), the car's MCU sends an event.
* **How it looks:** `mBodyworkDevice.registerListener(mAbsBYDAutoBodyworkListener, new int[]{BYDAutoFeatureIds.BODYWORK_POWER_LEVEL});`
* **Use case:** We use this to update our UI when the user physically interacts with the car. It is **not** used to send commands.

### B. Getters (`get`)
* **What it does:** Synchronously reads the current state of a hardware component.
* **How it looks:** `int temp = mAcDevice.get(new int[]{BYDAutoFeatureIds.Ac.TARGET_TEMPERATURE}, Integer.TYPE).intValue;`
* **Use case:** Reading initial states when our app launches.

### C. Setters (`set`)
* **What it does:** Sends a command to the MCU to change a hardware state. 
* **How it looks:** 
  ```java
  BYDAutoEventValue value = new BYDAutoEventValue();
  value.intValue = 1; // e.g., 1 for "Window Down"
  mBodyworkDevice.set(new int[]{BYDAutoFeatureIds.Bodywork.WINDOW_STATUS}, value);
  ```
* **Use case:** This is what we will use to actively control the car from our app.

## 2. Managing `BYDAutoFeatureIds`

You asked: *"Is it possible to get the values from `android.hardware.bydauto.BYDAutoFeatureIds`?"*

Yes! `BYDAutoFeatureIds` is a class inside the BYD framework that holds hundreds of `static final int` constants (like `0x010101`). 

### The Compiler Inline Problem
In Java, if you declare a `public static final int CONSTANT = 5;`, the Java compiler will literally replace every usage of `CONSTANT` with the number `5` in the compiled `.dex` file. 

If we create a fake stub class like this:
```java
public class BYDAutoFeatureIds {
    public static final int WINDOW_STATUS = 0; // Fake value
}
```
When we compile our app, our code will send `0` to the car instead of the real ID, because the compiler hardcoded the `0`!

### The Solution: Non-Final Stubs
To force our application to read the *real* values from the car's framework at runtime, we define our stubs **without** the `final` keyword and without assigning a value:

```java
public class BYDAutoFeatureIds {
    public static class Bodywork {
        public static int WINDOW_STATUS; // Not final, no value!
    }
}
```
Because it is not `final`, the Kotlin compiler cannot inline it. Instead, it generates an instruction to look up `WINDOW_STATUS` dynamically. When the proxy runs on the car, the Android Classloader loads the *real* `BYDAutoFeatureIds` class from `services.jar`, and our code successfully reads the exact, authentic integer IDs required to control the car!
