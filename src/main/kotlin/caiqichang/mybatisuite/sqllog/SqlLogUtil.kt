package caiqichang.mybatisuite.sqllog

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow

object SqlLogUtil {

    // using individual SQL log for each project, project path as key
    private val sqlLogMap = mutableMapOf<String, SqlLog>()

    // make it easy to get current tool window
    private val toolWindowMap = mutableMapOf<String, ToolWindow>()

    fun getSqlLog(project: Project?) = if (project == null) null else sqlLogMap[project.basePath]

    fun addSqlLog(project: Project?, consoleView: ConsoleViewImpl): SqlLog? {
        if (project != null) {
            val path = project.basePath
            if (path != null) {
                // renew if exist
                sqlLogMap[path] = SqlLog(consoleView)
                return sqlLogMap[path]
            }
        }
        return null
    }

    fun getToolWindow(project: Project?) = if (project == null) null else toolWindowMap[project.basePath]

    fun addToolWindow(project: Project?, toolWindow: ToolWindow): ToolWindow? {
        if (project != null) {
            val path = project.basePath
            if (path != null) {
                // renew if exist
                toolWindowMap[path] = toolWindow
                return toolWindowMap[path]
            }
        }
        return null
    }
}