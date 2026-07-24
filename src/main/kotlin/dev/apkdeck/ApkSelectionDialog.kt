package dev.apkdeck

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

internal class ApkSelectionDialog(
    project: Project,
    private val moduleDisplayName: String,
    options: List<String>,
) : DialogWrapper(project) {
    private val apkComboBox = ComboBox(options.toTypedArray())

    val selectedIndex: Int
        get() = apkComboBox.selectedIndex

    init {
        title = "Choose APK"
        setOKButtonText("Select")
        init()
    }

    override fun createCenterPanel(): JComponent =
        JPanel(BorderLayout(0, JBUI.scale(8))).apply {
            border = JBUI.Borders.empty(4)
            add(JLabel("Multiple APKs found for $moduleDisplayName:"), BorderLayout.NORTH)
            add(apkComboBox, BorderLayout.CENTER)
        }
}
