package dev.apkdeck

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader

class OpenApkDeckAction : AnAction(
    "APK Deck",
    "Inspect and manage project applications on connected Android devices",
    IconLoader.getIcon("/icons/apkDeck.svg", OpenApkDeckAction::class.java),
) {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val basePath = project.basePath

        ProgressManager.getInstance().run(object : Task.Modal(project, "APK Deck: Connecting…", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Connecting to ADB…"
                val adb = AdbService.adbPath(basePath)
                val serials = AdbService.getConnectedDevices(basePath)
                // serial → "ModelName (serial)" display name
                val deviceNames: Map<String, String> = serials.associateWith { serial ->
                    AdbService.getDeviceName(serial, adb)
                }
                indicator.text = "Scanning Android application modules…"
                val projectApps = ApplicationModuleScanner.scan(project)

                ApplicationManager.getApplication().invokeLater {
                    if (serials.isEmpty()) {
                        Messages.showInfoMessage(
                            project,
                            "No ADB device connected.",
                            "APK Deck",
                        )
                        return@invokeLater
                    }
                    ApkDeckDialog(project, projectApps, deviceNames, basePath).show()
                }
            }
        })
    }
}
