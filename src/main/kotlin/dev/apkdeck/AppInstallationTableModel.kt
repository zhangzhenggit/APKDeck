package dev.apkdeck

import java.util.Locale
import javax.swing.table.AbstractTableModel

data class TableRow(val info: AppInstallInfo)

class AppInstallationTableModel : AbstractTableModel() {

    internal val rows: MutableList<TableRow> = mutableListOf()
    private var projectItems: List<AppInstallInfo> = emptyList()

    val visibleItems: List<AppInstallInfo>
        get() = rows.map { it.info }

    fun resetItems(projectItems: List<AppInstallInfo>) {
        this.projectItems = projectItems
        rebuildRows()
    }

    private fun rebuildRows() {
        rows.clear()
        projectItems
            .sortedWith(
                compareBy<AppInstallInfo> { it.moduleName.lowercase(Locale.ROOT) }
                    .thenBy { it.packageName.lowercase(Locale.ROOT) },
            )
            .forEach { rows.add(TableRow(it)) }
        fireTableDataChanged()
    }

    fun updateRow(
        packageName: String,
        newStatus: InstallStatus,
        activeApkPaths: List<String>? = null,
    ) {
        val index = rows.indexOfFirst { it.info.packageName == packageName }
        val info = rows.firstOrNull { it.info.packageName == packageName }?.info
            ?: projectItems.firstOrNull { it.packageName == packageName }
            ?: return
        info.status = newStatus
        if (activeApkPaths != null) info.activeApkPaths = activeApkPaths
        if (index >= 0) fireTableRowsUpdated(index, index) else rebuildRows()
    }

    fun notifyRowChanged(packageName: String) {
        val index = rows.indexOfFirst { it.info.packageName == packageName }
        if (index >= 0) fireTableRowsUpdated(index, index)
    }

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = COLUMNS.size

    override fun getColumnName(column: Int): String = COLUMNS[column]

    override fun getColumnClass(column: Int): Class<*> = String::class.java

    override fun isCellEditable(row: Int, column: Int): Boolean =
        column in ACTION_COLUMNS

    override fun getValueAt(row: Int, column: Int): Any {
        val item = rows[row].info
        return when (column) {
            COL_APPLICATION -> item.moduleName.ifEmpty { item.packageName }
            COL_INSTALLATION -> statusText(item)
            else -> ""
        }
    }

    companion object {
        const val COL_APPLICATION = 0
        const val COL_INSTALLATION = 1
        const val COL_REINSTALL = 2
        const val COL_CLEAR = 3
        const val COL_UNINSTALL = 4
        const val COL_PUSH = 5

        val ACTION_COLUMNS = setOf(COL_REINSTALL, COL_CLEAR, COL_UNINSTALL, COL_PUSH)
        val COLUMNS = arrayOf("Application", "Installation", "", "", "", "")

        private fun statusText(info: AppInstallInfo): String = when (info.status) {
            InstallStatus.USER_APP -> "Installed"
            InstallStatus.UPDATED_SYSTEM_APP -> "Installed · system update"
            InstallStatus.SYSTEM_APP -> "System app"
            InstallStatus.NOT_INSTALLED -> "Not installed"
            InstallStatus.UNKNOWN -> "Querying…"
        }
    }
}
