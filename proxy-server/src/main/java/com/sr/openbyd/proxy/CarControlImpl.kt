package com.sr.openbyd.proxy

import android.content.Context
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.util.Log
import android.view.Surface
import com.sr.openbyd.CarConstants
import com.sr.openbyd.ipc.ICarControl
import java.util.concurrent.ConcurrentHashMap
import android.hardware.bydauto.instrument.BYDAutoInstrumentDevice
import android.view.MotionEvent
import android.view.InputDevice

object SystemContext {
    fun get(): Context {
        val activityThread = Class.forName("android.app.ActivityThread")
        val currentActivityThread =
            activityThread.getMethod("currentActivityThread")
                .invoke(null)

        return activityThread
            .getMethod("getSystemContext")
            .invoke(currentActivityThread) as Context
    }
}

class CarControlImpl : ICarControl.Stub() {
    private val context by lazy { SystemContext.get() }
    private val virtualDisplays = ConcurrentHashMap<Int, VirtualDisplay>()

    override fun getApiVersion(): Int {
        return 1
    }

    override fun ping(): String {
        val result = StringBuilder()
        result.appendLine("Ping received on proxy side!")
        result.appendLine("Attempting direct device instantiation...")

        try {
            val device = android.hardware.bydauto.bodywork.BYDAutoBodyworkDevice.getInstance(context)
            result.appendLine("Instance acquired successfully: $device")

            // Just double-checking we can call one of the getters directly
            val isWindowOpen = device.getWindowState(1)
            result.appendLine("Tested direct call - WindowState: $isWindowOpen")
            result.appendLine("SUCCESS")
        } catch (e: Exception) {
            result.appendLine("FAILURE")
            result.appendLine("Ping test failed:")
            result.appendLine(e.message)
            result.appendLine(e.stackTraceToString())
        }

        return result.toString()
    }

    override fun setWindow(windowId: Int, state: Int): String {
        val result = StringBuilder()
        result.appendLine("Executing setWindow(windowId=$windowId, state=$state)")

        try {
            val device = android.hardware.bydauto.bodywork.BYDAutoBodyworkDevice.getInstance(context)

            if (windowId in 1..4) {
                // BYD Area IDs (1-4)
                result.appendLine("Calling native setBodyWindowCtrlState(area=$windowId, state=$state)...")

                val code = device.setBodyWindowCtrlState(windowId, state)
                result.appendLine("SUCCESS: Command sent. Returned code: $code")

            } else if (windowId == 5) {
                result.appendLine("Calling native setMoonRoofState($state)...")
                val code = device.setMoonRoofState(state)
                result.appendLine("SUCCESS: Command sent. Returned code: $code")
                
            } else if (windowId == 6) {
                result.appendLine("Calling native setSunshadeState($state)...")
                val code = device.setSunshadeState(state)
                result.appendLine("SUCCESS: Command sent. Returned code: $code")
                
            } else { // Do all the windows
                result.appendLine("Calling native setAllWindowState($state, $state, $state, $state)...")

                val code = device.setAllWindowState(state, state, state, state)
                result.appendLine("SUCCESS: Command sent. Returned code: $code")
            }
        } catch (e: Exception) {
            result.appendLine("FAILURE during native execution:")
            result.appendLine(e.message)
            result.appendLine(e.stackTraceToString())
        }

        return result.toString()
    }

    override fun setAcPower(powerOn: Boolean): String {
        val result = StringBuilder()
        result.appendLine("Executing setAcPower(powerOn=$powerOn)")

        try {
            val device = android.hardware.bydauto.ac.BYDAutoAcDevice.getInstance(context)
            if (powerOn) {
                device.start(1)
            } else {
                device.stop(1)
            }
            result.appendLine("SUCCESS: Command sent for ${if (powerOn) "ON" else "OFF"} AC")
        } catch (e: Exception) {
            result.appendLine("FAILURE during AC command execution:")
            result.appendLine(e.message)
            result.appendLine(e.stackTraceToString())
        }

        return result.toString()
    }

    override fun setAuxiliaryLight(powerOn: Boolean): String {
        val result = StringBuilder()
        result.appendLine("Executing setHazardLights(powerOn=$powerOn) ch")

        try {
            val device = android.hardware.bydauto.bodywork.BYDAutoBodyworkDevice.getInstance(context)
            result.appendLine("Prev: ${device.auxiliaryLightStatus}")
            val state = if (powerOn) 0 else 1
            val code = device.setAuxiliaryLight(state)
            result.appendLine("Post: ${device.auxiliaryLightStatus}")
            result.appendLine("SUCCESS: Command sent for ${if (powerOn) "ON" else "OFF"} ($state) hazard lights. Returned code: $code")
        } catch (e: Exception) {
            result.appendLine("FAILURE during hazard lights command execution:")
            result.appendLine(e.message)
            result.appendLine(e.stackTraceToString())
        }

        return result.toString()
    }

    override fun runShellCommand(command: String): String {
        val result = StringBuilder()
        return try {
            result.appendLine("Executing runShellCommand: $command")
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()
            process.waitFor()
            val output = buildString {
                if (stdout.isNotBlank()) appendLine(stdout.trim())
                if (stderr.isNotBlank()) appendLine("STDERR: ${stderr.trim()}")
            }.trim()
            result.appendLine("runShellCommand result: $output")
            result.toString()
        } catch (e: Exception) {
            result.appendLine("runShellCommand failed: ${e.message}")
            result.toString()
        }
    }

    override fun launchApp(packageName: String): String {
        val log = StringBuilder()

        // Strategy 1: resolve the exact component name then am start -n
        try {
            val resolve = exec("cmd package resolve-activity --brief -c android.intent.category.LAUNCHER $packageName")
            val component = resolve.lines()
                .firstOrNull { it.contains("/") && !it.startsWith("No ") }
                ?.trim()

            if (component != null) {
                val result = exec("am start -n $component")
                log.appendLine("Strategy 1 (resolve+am start -n $component): $result")
                if (!result.contains("Error")) return log.toString()
            } else {
                log.appendLine("Strategy 1: could not resolve component for $packageName")
            }
        } catch (e: Exception) {
            log.appendLine("Strategy 1 exception: ${e.message}")
        }

        // Strategy 2: am start without CATEGORY_LAUNCHER (just ACTION_MAIN + pkg)
        try {
            val result = exec("am start -a android.intent.action.MAIN $packageName")
            log.appendLine("Strategy 2 (am start -a MAIN): $result")
            if (!result.contains("Error")) return log.toString()
        } catch (e: Exception) {
            log.appendLine("Strategy 2 exception: ${e.message}")
        }

        // Strategy 3: monkey — the most reliable fallback, works for any installed package
        try {
            val result = exec("monkey -p $packageName -c android.intent.category.LAUNCHER 1")
            log.appendLine("Strategy 3 (monkey): $result")
            if (!result.contains("Error") && !result.contains("error")) return log.toString()
        } catch (e: Exception) {
            log.appendLine("Strategy 3 exception: ${e.message}")
        }

        // Strategy 4: monkey without category filter (absolute last resort)
        try {
            val result = exec("monkey -p $packageName 1")
            log.appendLine("Strategy 4 (monkey no-category): $result")
        } catch (e: Exception) {
            log.appendLine("Strategy 4 exception: ${e.message}")
        }

        return log.toString()
    }

    /** Runs a shell command and returns stdout+stderr as a single string. */
    private fun exec(command: String): String {
        val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
        val stdout = process.inputStream.bufferedReader().readText().trim()
        val stderr = process.errorStream.bufferedReader().readText().trim()
        process.waitFor()
        return buildString {
            if (stdout.isNotBlank()) append(stdout)
            if (stderr.isNotBlank()) {
                if (isNotEmpty()) append(" | STDERR: ")
                append(stderr)
            }
        }.ifEmpty { "OK" }
    }

    override fun createVirtualDisplay(name: String, width: Int, height: Int, density: Int, surface: Surface, flags: Int): String {
        val logs = StringBuilder()

        logs.appendLine("1. createVirtualDisplay request: $name ${width}x$height dpi=$density flags=$flags")

        if (!surface.isValid) {
            logs.appendLine("2. ERROR: Surface is INVALID")
            return logs.toString()
        }

        return try {
            logs.appendLine("3. Creating shell context for com.android.shell")

            val shellContext = context.createPackageContext(CarConstants.PACKAGE_SHELL, 0)

            logs.appendLine("4. Getting DisplayManager service")

            val dm = shellContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

            logs.appendLine("5. Calling dm.createVirtualDisplay(...)")

            val vd = dm.createVirtualDisplay(name, width, height, density, surface, flags)

            if (vd == null) {
                logs.appendLine("6. ERROR: dm.createVirtualDisplay returned NULL")
                return logs.toString()
            }

            logs.appendLine("7. VirtualDisplay object created")

            val display = vd.display
            val id = display.displayId

            logs.appendLine("8. Display obtained: id=$id name=${display.name}")

            if (id == 0 || id == -1) {
                logs.appendLine("9. ERROR: Created display has invalid ID: $id (releasing...)")
                vd.release()
                logs.appendLine("10. VirtualDisplay released")
                return logs.toString()
            }

            virtualDisplays[id] = vd

            Log.d("CarControlImpl", "SUCCESS: Created VirtualDisplay '$name' ID=$id Name=${display.name}")
            
            // Return only the ID as the last line for easy parsing
            logs.appendLine("RESULT_ID:$id")

            logs.toString()

        } catch (e: Exception) {
            logs.appendLine("13. EXCEPTION in createVirtualDisplay: ${e.message}")
            logs.appendLine("14. StackTrace: ${Log.getStackTraceString(e)}")
            logs.toString()
        }
    }

    override fun releaseVirtualDisplay(displayId: Int): String {
        val vd = virtualDisplays.remove(displayId)
        return if (vd != null) {
            vd.release()
            Log.d("CarControlImpl", "Released VirtualDisplay ID=$displayId")
            "Successfully released VirtualDisplay ID=$displayId"
        } else {
            "VirtualDisplay ID=$displayId not found for release"
        }
    }

    override fun moveTaskToDisplay(taskId: Int, displayId: Int): String {
        return try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val iAtm = atmClass.getMethod("getService").invoke(null)
            
            val methods = iAtm.javaClass.methods
            // Use moveRootTaskToDisplay which was found in your logs and worked!
            val target = methods.find { it.name == "moveRootTaskToDisplay" && it.parameterTypes.size == 2 }
                ?: methods.find { it.name == "moveTaskToDisplay" && it.parameterTypes.size == 2 }

            if (target != null) {
                target.invoke(iAtm, taskId, displayId)
                "SUCCESS: moveTaskToDisplay via ${target.name}"
            } else {
                "ERROR: moveTaskToDisplay method not found"
            }
        } catch (e: Exception) {
            "ERROR: moveTaskToDisplay failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    override fun setTaskWindowingMode(taskId: Int, windowingMode: Int): String {
        return try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val iAtm = atmClass.getMethod("getService").invoke(null)
            val setTaskWindowingMode = iAtm.javaClass.getMethod("setTaskWindowingMode", Int::class.java, Int::class.java, Boolean::class.javaPrimitiveType)
            setTaskWindowingMode.invoke(iAtm, taskId, windowingMode, true)
            "SUCCESS: setTaskWindowingMode"
        } catch (e: Exception) {
            "ERROR: setTaskWindowingMode failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    override fun setTaskActivityType(taskId: Int, activityType: Int): String {
        return "SKIP"
    }

    override fun setTaskBounds(taskId: Int, left: Int, top: Int, right: Int, bottom: Int): String {
        return try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val iAtm = atmClass.getMethod("getService").invoke(null)
            // Use resizeTask which was found in your logs
            val resizeTask = iAtm.javaClass.getMethod("resizeTask", Int::class.java, Rect::class.java, Int::class.javaPrimitiveType)
            if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                resizeTask.invoke(iAtm, taskId, null, 1)
            } else {
                resizeTask.invoke(iAtm, taskId, Rect(left, top, right, bottom), 1)
            }
            "SUCCESS: resizeTask"
        } catch (e: Exception) {
            "ERROR: resizeTask failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    override fun setFocusedTask(taskId: Int): String {
        return try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val iAtm = atmClass.getMethod("getService").invoke(null)
            // Use setFocusedRootTask which was found in your logs
            val setFocusedRootTask = iAtm.javaClass.getMethod("setFocusedRootTask", Int::class.java)
            setFocusedRootTask.invoke(iAtm, taskId)
            "SUCCESS: setFocusedRootTask"
        } catch (e: Exception) {
            "ERROR: setFocusedRootTask failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    override fun getTopActivityPackage(): String {
        return try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val iAtm = atmClass.getMethod("getService").invoke(null)
            val getTasks = iAtm.javaClass.getMethod("getTasks", Int::class.java, Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
            val tasks = getTasks.invoke(iAtm, 1, false, false) as List<*>
            
            if (tasks.isNotEmpty()) {
                val task = tasks[0]
                fun findField(obj: Any, name: String): java.lang.reflect.Field? {
                    var curr: Class<*>? = obj.javaClass
                    while (curr != null) {
                        try {
                            return curr.getDeclaredField(name)
                        } catch (e: NoSuchFieldException) {}
                        curr = curr.superclass
                    }
                    return null
                }
                val topActivity = findField(task!!, "topActivity")?.let { it.isAccessible = true; it.get(task) as? android.content.ComponentName }
                topActivity?.packageName ?: "unknown"
            } else {
                "none"
            }
        } catch (e: Exception) {
            "error: ${e.message}"
        }
    }

    override fun getTaskId(packageName: String): String {
        val logs = StringBuilder()
        logs.appendLine("getTaskId searching for: $packageName")
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val iAtm = atmClass.getMethod("getService").invoke(null)
            val getTasks = iAtm.javaClass.getMethod("getTasks", Int::class.java, Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
            val tasks = getTasks.invoke(iAtm, 50, false, false) as List<*>
            
            logs.appendLine("Found ${tasks.size} tasks in system")

            for (task in tasks) {
                if (task == null) continue
                
                // Reflection helper to find field in class or parents
                fun findField(obj: Any, name: String): java.lang.reflect.Field? {
                    var curr: Class<*>? = obj.javaClass
                    while (curr != null) {
                        try {
                            return curr.getDeclaredField(name)
                        } catch (e: NoSuchFieldException) {}
                        curr = curr.superclass
                    }
                    return null
                }

                val baseActivity = findField(task, "baseActivity")?.let { it.isAccessible = true; it.get(task) as? android.content.ComponentName }
                val topActivity = findField(task, "topActivity")?.let { it.isAccessible = true; it.get(task) as? android.content.ComponentName }
                val taskId = findField(task, "taskId")?.let { it.isAccessible = true; it.get(task) as? Int } ?: 0
                val stackId = findField(task, "stackId")?.let { it.isAccessible = true; it.get(task) as? Int } ?: 0
                val displayId = findField(task, "displayId")?.let { it.isAccessible = true; it.get(task) as? Int } ?: 0

                logs.appendLine("  - Task: pkg=${baseActivity?.packageName} top=${topActivity?.packageName} taskId=$taskId stackId=$stackId displayId=$displayId")

                if (baseActivity?.packageName == packageName || topActivity?.packageName == packageName) {
                    val resultId = if (taskId > 0) taskId else stackId
                    logs.appendLine("MATCH found: ID=$resultId")
                    logs.appendLine("RESULT_ID:$resultId")
                    return logs.toString()
                }
            }
            logs.appendLine("No task match found for $packageName")
            logs.appendLine("RESULT_ID:-1")
            return logs.toString()
        } catch (e: Exception) {
            logs.appendLine("getTaskId failed: ${e.message}")
            logs.appendLine("RESULT_ID:-1")
            return logs.toString()
        }
    }

    override fun launchAndForce(packageName: String, displayId: Int, width: Int, height: Int): String {
        val result = StringBuilder()
        result.appendLine("Executing launchAndForce(pkg=$packageName, display=$displayId, ${width}x${height})")

        try {
            // 1. Check if already open
            var taskId = getTaskId(packageName).lines()
                .firstOrNull { it.startsWith("RESULT_ID:") }
                ?.substringAfter("RESULT_ID:")
                ?.toIntOrNull() ?: -1

            if (taskId == -1 || taskId == 0) {
                // 2. Launch the app via shell if not open
                result.appendLine("1. App not found, launching...")
                val launchOutput = launchApp(packageName)
                result.appendLine("Launch log:\n$launchOutput")

                // 3. Wait for task to appear
                result.appendLine("2. Searching for taskId...")
                for (i in 1..15) {
                    val taskLogs = getTaskId(packageName)
                    taskId = taskLogs.lines()
                        .firstOrNull { it.startsWith("RESULT_ID:") }
                        ?.substringAfter("RESULT_ID:")
                        ?.toIntOrNull() ?: -1

                    if (taskId != -1 && taskId != 0) {
                        result.appendLine("Task discovery success on attempt $i")
                        break
                    }
                    Thread.sleep(500)
                }
                Thread.sleep(1500)
            } else {
                result.appendLine("1. App already open with ID $taskId")
            }

            if (taskId == -1 || taskId == 0) {
                result.appendLine("FAILED: Could not find valid taskId/stackId for $packageName after launch")
                return result.toString()
            }

            // 4. Persistence Loop: The system might redirect the app back to Display 0 during startup.
            // We apply the move-resize-focus sequence multiple times over 3 seconds.
            result.appendLine("3. Starting persistence redirection loop...")
            
            for (i in 1..2) {
                result.appendLine("Attempt $i at redirection:")
                val moveRes = moveTaskToDisplay(taskId, displayId)
                result.appendLine(moveRes)
                
                // If it fails with 'null' or error, it might be already there or busy,
                // but we always try to enforce bounds and focus.
                setTaskBounds(taskId, 0, 0, width, height)
                setFocusedTask(taskId)
                
                Thread.sleep(200)
            }

            result.appendLine("FINISH: launchAndForce sequence complete.")
        } catch (e: Exception) {
            result.appendLine("ERROR in launchAndForce: ${e.message}")
            result.appendLine(Log.getStackTraceString(e))
        }

        return result.toString()
    }

    private fun queryInstrumentFeatureLive(featureId: Int): Int {
        try {
            val instrumentDevice = BYDAutoInstrumentDevice.getInstance(context) ?: return 0
            val eventValue = instrumentDevice.get(intArrayOf(featureId), Integer.TYPE)
            if (eventValue != null) {
                val value = eventValue.intValue
                return value
            }
        } catch (e: Throwable) {
            Log.e("CarControlImpl", "Failed to query live instrument feature value: $featureId", e)
        }
        return 0
    }

    override fun getInstrumentFeatureValue(featureId: Int): Int {
        return queryInstrumentFeatureLive(featureId)
    }

    private fun querySettingFeatureLive(featureId: Int): String {
        val result = StringBuilder()
        try {
            val settingDeviceClass = Class.forName("android.hardware.bydauto.setting.BYDAutoSettingDevice")
            val getInstanceMethod = settingDeviceClass.getMethod("getInstance", Context::class.java)
            val deviceInstance = getInstanceMethod.invoke(null, context) ?: return "ERROR: SettingDevice is null"
            
            val getMethod = settingDeviceClass.getMethod("get", IntArray::class.java, Class::class.java)
            val eventValue = getMethod.invoke(deviceInstance, intArrayOf(featureId), Integer.TYPE)
            if (eventValue != null) {
                val intValueField = eventValue.javaClass.getField("intValue")
                val value = intValueField.get(eventValue) as Int
                result.appendLine("SUCCESS: setting feature $featureId value is $value")
                result.append("RESULT_VALUE:$value")
            } else {
                result.append("ERROR: eventValue is null")
            }
        } catch (e: Throwable) {
            result.append("ERROR: ${e.message}")
        }
        return result.toString()
    }

    override fun getSettingFeatureValue(featureId: Int): String {
        return querySettingFeatureLive(featureId)
    }

    override fun setInstrumentFeatureValue(featureId: Int, value: Int): String {
        try {
            val instrumentDevice = BYDAutoInstrumentDevice.getInstance(context) ?: return "ERROR: InstrumentDevice is null"
            val eventValue = android.hardware.bydauto.BYDAutoEventValue().apply {
                intValue = value
            }
            val result = instrumentDevice.set(intArrayOf(featureId), eventValue)
            return "SUCCESS: set instrument feature $featureId to $value -> result code $result"
        } catch (e: Throwable) {
            return "ERROR: ${e.message}"
        }
    }

    override fun setSettingFeatureValue(featureId: Int, value: Int): String {
        try {
            val settingDeviceClass = Class.forName("android.hardware.bydauto.setting.BYDAutoSettingDevice")
            val getInstanceMethod = settingDeviceClass.getMethod("getInstance", Context::class.java)
            val deviceInstance = getInstanceMethod.invoke(null, context) ?: return "ERROR: SettingDevice is null"
            
            val eventValue = android.hardware.bydauto.BYDAutoEventValue().apply {
                intValue = value
            }
            val setMethod = settingDeviceClass.getMethod("set", IntArray::class.java, android.hardware.bydauto.BYDAutoEventValue::class.java)
            val result = setMethod.invoke(deviceInstance, intArrayOf(featureId), eventValue)
            return "SUCCESS: set setting feature $featureId to $value -> result code $result"
        } catch (e: Throwable) {
            return "ERROR: ${e.message}"
        }
    }

    override fun getSystemProperty(key: String): String {
        return try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getMethod("get", String::class.java)
            getMethod.invoke(null, key) as String
        } catch (e: Exception) {
            Log.e("CarControlImpl", "Failed to get system property for key $key", e)
            ""
        }
    }

    override fun scrapBydAuto(): String {
        val result = StringBuilder()
        result.appendLine("==================================================")
        result.appendLine("   BYD INSTRUMENT DEVICE REFLECTION SCRAPER       ")
        result.appendLine("==================================================")

        val targetClass = "android.hardware.bydauto.instrument.BYDAutoInstrumentDevice"
        try {
            val clazz = Class.forName(targetClass)
            result.appendLine("\n[CLASS] ${clazz.name}")

            // 1. Superclass Hierarchy
            var superClazz = clazz.superclass
            val superChain = mutableListOf<String>()
            while (superClazz != null) {
                superChain.add(superClazz.name)
                superClazz = superClazz.superclass
            }
            if (superChain.isNotEmpty()) {
                result.appendLine("  Extends: ${superChain.joinToString(" -> ")}")
            }

            // 2. Implemented Interfaces
            val interfaces = clazz.interfaces
            if (interfaces.isNotEmpty()) {
                result.appendLine("  Implements Interfaces: ${interfaces.joinToString(", ") { it.name }}")
            }

            // 3. Try to get active instance
            try {
                val getInstance = clazz.getMethod("getInstance", Context::class.java)
                val instance = getInstance.invoke(null, context)
                result.appendLine("  Instance acquired successfully: $instance")
            } catch (e: Throwable) {
                result.appendLine("  Instance: Could not be acquired dynamically (${e.message})")
            }

            // 4. Detailed Methods Dump (Declared and Inherited)
            result.appendLine("\n[METHODS DUMP]")
            var currentClass: Class<*>? = clazz
            while (currentClass != null && currentClass != Any::class.java) {
                result.appendLine("\n  --- Methods from: ${currentClass.name} ---")
                currentClass.declaredMethods.sortedBy { it.name }.forEach { method ->
                    try {
                        val params = method.parameterTypes.joinToString(", ") { it.name }
                        val returnType = method.returnType.name
                        val modifiers = java.lang.reflect.Modifier.toString(method.modifiers)
                        result.appendLine("  $modifiers $returnType ${method.name}($params)")
                    } catch (e: Throwable) {
                        result.appendLine("  (Failed to parse method: ${e.message})")
                    }
                }
                currentClass = currentClass.superclass
            }

            // 5. Declared Fields
            result.appendLine("\n[FIELDS DUMP]")
            var currentClassFields: Class<*>? = clazz
            while (currentClassFields != null && currentClassFields != Any::class.java) {
                result.appendLine("\n  --- Fields from: ${currentClassFields.name} ---")
                currentClassFields.declaredFields.sortedBy { it.name }.forEach { field ->
                    try {
                        val type = field.type.name
                        val modifiers = java.lang.reflect.Modifier.toString(field.modifiers)
                        result.appendLine("  $modifiers $type ${field.name}")
                    } catch (e: Throwable) {
                        result.appendLine("  (Failed to parse field: ${e.message})")
                    }
                }
                currentClassFields = currentClassFields.superclass
            }

            // 6. Inner Classes and Interfaces
            result.appendLine("\n[INNER CLASSES / INTERFACES]")
            clazz.declaredClasses.forEach { inner ->
                result.appendLine("  - Inner Class: ${inner.name}")
                inner.declaredMethods.forEach { m ->
                    val p = m.parameterTypes.joinToString(", ") { it.name }
                    result.appendLine("    * ${m.name}($p) : ${m.returnType.name}")
                }
            }

        } catch (e: ClassNotFoundException) {
            result.appendLine("\n[FATAL] Class $targetClass not found in current ROM classpath!")
        } catch (e: Throwable) {
            result.appendLine("\n[FATAL ERROR] Failed to reflect class: ${e.message}")
        }

        return result.toString()
    }

    private var inputManager: Any? = null
    private var injectInputEventMethod: java.lang.reflect.Method? = null
    private var setDisplayIdMethod: java.lang.reflect.Method? = null
    private var isInputInitialized = false
    private var initError: String? = null

    private fun initInputInjection() {
        if (isInputInitialized) return
        try {
            val imClass = Class.forName("android.hardware.input.InputManager")
            val getInstance = imClass.getMethod("getInstance")
            inputManager = getInstance.invoke(null)
            injectInputEventMethod = imClass.getMethod(
                "injectInputEvent",
                android.view.InputEvent::class.java,
                Int::class.javaPrimitiveType
            )
            setDisplayIdMethod = MotionEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            isInputInitialized = true
            Log.d("CarControlImpl", "Input injection reflection initialized successfully.")
        } catch (e: Exception) {
            val sw = java.io.StringWriter()
            val pw = java.io.PrintWriter(sw)
            e.printStackTrace(pw)
            initError = "init failed: ${e.message}\n$sw"
            Log.e("CarControlImpl", "Failed to initialize input injection reflection: ${e.message}", e)
        }
    }

    override fun injectTouchEvent(displayId: Int, action: Int, downTime: Long, eventTime: Long, x: Float, y: Float): String {
        val logBuilder = StringBuilder()
        logBuilder.appendLine("injectTouchEvent: action=$action, displayId=$displayId, x=$x, y=$y")

        initInputInjection()
        if (initError != null) {
            logBuilder.appendLine("ERROR: $initError")
        }

        val im = inputManager
        val injectMethod = injectInputEventMethod
        if (im == null || injectMethod == null) {
            logBuilder.appendLine("ERROR: InputManager reflection failed to initialize. im=$im, injectMethod=$injectMethod")
            return logBuilder.toString()
        }

        return try {
            // Build MotionEvent.PointerProperties
            val properties = arrayOf(MotionEvent.PointerProperties().apply {
                id = 0
                toolType = MotionEvent.TOOL_TYPE_FINGER
            })

            // Build MotionEvent.PointerCoords
            val coords = arrayOf(MotionEvent.PointerCoords().apply {
                this.x = x
                this.y = y
                pressure = 1.0f
                size = 1.0f
            })

            // Obtain a realistic touchscreen MotionEvent
            val motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                action,
                1,              // pointerCount
                properties,
                coords,
                0,              // metaState
                0,              // buttonState
                1.0f,           // xPrecision
                1.0f,           // yPrecision
                0,              // deviceId
                0,              // edgeFlags
                InputDevice.SOURCE_TOUCHSCREEN, // source (4098)
                0               // flags
            )

            // Set the target display ID via reflection
            if (setDisplayIdMethod != null) {
                setDisplayIdMethod?.invoke(motionEvent, displayId)
            } else {
                logBuilder.appendLine("WARNING: setDisplayIdMethod is null, displayId $displayId may not be set on MotionEvent!")
            }

            // Inject the event asynchronously (mode = 0: INJECT_INPUT_EVENT_MODE_ASYNC)
            val result = injectMethod.invoke(im, motionEvent, 0)
            motionEvent.recycle()

            logBuilder.appendLine("SUCCESS: Injection returned result: $result")
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP || result == false) {
                Log.d("CarControlImpl", "injectTouchEvent: action=$action, displayId=$displayId, x=$x, y=$y, result=$result")
            }
            logBuilder.toString()
        } catch (e: Exception) {
            val sw = java.io.StringWriter()
            val pw = java.io.PrintWriter(sw)
            e.printStackTrace(pw)
            logBuilder.appendLine("EXCEPTION: injectTouchEvent failed: ${e.message}\n$sw")
            Log.e("CarControlImpl", "injectTouchEvent failed for action=$action displayId=$displayId: ${e.message}", e)
            logBuilder.toString()
        }
    }
}


