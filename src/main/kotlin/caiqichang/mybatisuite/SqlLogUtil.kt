package caiqichang.mybatisuite

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.JBColor
import java.awt.Font

object SqlLogUtil {

    var running = false

    private const val SQL_PREFIX = "==>  Preparing:"
    private const val PARAM_PREFIX = "==> Parameters:"

    private var sql = ""

    // types that need to add single quote
    private var stringType = listOf("String", "Date", "Time", "LocalDate", "LocalTime", "LocalDateTime", "BigDecimal", "Timestamp")

    fun handleLog(log: String) {
        if (!running) return

        // handle sql line
        if (log.startsWith(SQL_PREFIX)) {
            sql = log.replaceFirst(SQL_PREFIX, "").trim()
            return
        }

        // handle param line
        if (log.startsWith(PARAM_PREFIX) && sql.isNotBlank()) {
            var paramStr = log.replaceFirst(PARAM_PREFIX, "").trim()

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
            showSQL(sql)
        }
    }

    // ---- UI ----

    private var count = 1

    private fun showSQL(fullSql: String) {
        consoleView?.print("---- ${count++} ----\n", ConsoleViewContentType("Green", TextAttributes(JBColor.GREEN, null, null, null, Font.PLAIN)))
        consoleView?.print(fullSql, ConsoleViewContentType.NORMAL_OUTPUT)
        consoleView?.print("\n\n", ConsoleViewContentType.NORMAL_OUTPUT)

        // clear sql
        sql = ""
    }

    private var init = false
    private var consoleView: ConsoleViewImpl? = null

    fun init(project: Project) {
        if (init) return
        init = true
        ApplicationManager.getApplication().invokeLater {
            consoleView = ConsoleViewImpl(project, true)
            
            val contentManager = ToolWindowManager.getInstance(project).getToolWindow("MyBatis")?.contentManager
            
            val runnerPanel = RunnerLayoutUi.Factory.getInstance(project).create("1", "2", "3") {}
            
            runnerPanel.addContent(
                runnerPanel.createContent("4", consoleView!!.component, "Log", null, consoleView?.component).apply { 
                    isCloseable = false
                }
            )
            
            runnerPanel.options.setLeftToolbar(DefaultActionGroup().apply {
                addAction(SqlLogAction())
                addAction(object : ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE) {
                    override fun getEditor(e: AnActionEvent): Editor? {
                        return consoleView?.editor
                    }
                })
                addAction(ScrollToTheEndToolbarAction(consoleView?.editor!!))
                addAction(object: AnAction("Clear All", "", AllIcons.Actions.GC) {
                    override fun actionPerformed(e: AnActionEvent) {
                        consoleView?.clear()
                    }
                })
            }, "6")
            
            contentManager?.addContent(
                contentManager.factory.createContent(runnerPanel.component, "SQL", false)
            )
        }
    }
}