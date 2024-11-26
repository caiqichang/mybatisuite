package caiqichang.mybatisuite.sqllog

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.project.Project

object SqlLogUtil {
    private val logList = mutableMapOf<String, SqlLog>()

    fun getLog(project: Project?): SqlLog? {
        if (project == null) return null
        return logList[project.basePath]
    }

    fun addLog(project: Project?, consoleView: ConsoleViewImpl): SqlLog? {
        if (project != null) {
            val path = project.basePath
            if (path != null) {
                if (logList[path] == null) {
                    logList[path] = SqlLog(consoleView)
                }
                return logList[path]
            }
        }
        return null
    }
}