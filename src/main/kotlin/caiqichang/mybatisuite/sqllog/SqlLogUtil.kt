package caiqichang.mybatisuite.sqllog

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

object SqlLogUtil {

    var running = false

    private const val sqlPrefix = "==>  Preparing:"
    private const val paramPrefix = "==> Parameters:"

    private var sql = ""

    // types that need to add single quote
    private var stringType = listOf("String", "Date", "Time", "LocalDate", "LocalTime", "LocalDateTime", "BigDecimal", "Timestamp")

    fun handleLog(log: String) {
        if (!running) return;

        // handle sql line
        if (log.startsWith(sqlPrefix)) {
            sql = log.replaceFirst(sqlPrefix, "").trim()
            return;
        }

        // handle param line
        if (log.startsWith(paramPrefix) && sql.isNotBlank()) {
            var paramStr = log.replaceFirst(paramPrefix, "").trim()

            // remove last bracket
            if (paramStr.endsWith(")")) paramStr = paramStr.substring(0, paramStr.length - 1)

            if (paramStr.isNotBlank()) {
                val paramList = paramStr.split("), ").map {
                    // get value and type
                    var value = it.substring(0, it.lastIndexOf("("))
                    val type = it.substring(it.lastIndexOf("(")).replaceFirst("(", "")

                    if (stringType.contains(type)) {
                        // escape single quote and add single quote to both sides
                        value = "'${value.replace("'", "''")}'"
                    }

                    return@map value
                }

                val pos = mutableListOf<Int>()
                var idx = sql.indexOf("?")
                while (idx > 0) {
                    pos += idx
                    idx = sql.indexOf("?", idx + 1)
                }

                if (pos.isNotEmpty()) {
                    var fullSql = ""
                    var lastIndex = 0
                    pos.forEachIndexed { index, it ->
                        fullSql += "${sql.substring(lastIndex, it)}${if (paramList.size > index) paramList[index] else "?"}"
                        lastIndex = it + 1
                    }
                    if (sql.length > lastIndex) {
                        fullSql += sql.substring(lastIndex, sql.length)
                    }

                    showSQL(fullSql)
                    return
                }
            }

            // sql only, no param
            showSQL("" + sql)
        }
    }

    // ---- UI ----
    
    private var count = 1

    private fun showSQL(fullSql: String) {
        // clear sql
        sql = ""

        consoleView?.print(
            """
            ---- ${count++} ----
            ${fullSql}
        """.trimIndent(), ConsoleViewContentType.NORMAL_OUTPUT
        )

        consoleView?.print("\n\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    private var consoleView: ConsoleView? = null

    fun init(project: Project) {
        ApplicationManager.getApplication().invokeLater {
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

            val contentManager = ToolWindowManager.getInstance(project).getToolWindow("MyBatis")?.contentManager

            contentManager?.addContent(
                contentManager.factory.createContent(consoleView?.component, "SQL", false)
            )
        }
    }
}