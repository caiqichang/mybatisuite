package caiqichang.mybatisuite.sqllog

import caiqichang.mybatisuite.common.Icon
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.layout.impl.RunnerLayoutUiImpl
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class SqlLogToolWindowFactory : ToolWindowFactory {
    override fun init(toolWindow: ToolWindow) {
        super.init(toolWindow)

        toolWindow.setIcon(Icon.LOGO)
        toolWindow.stripeTitle = "MyBatis"
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        ApplicationManager.getApplication().invokeLater {
            SqlLogUtil.consoleView = ConsoleViewImpl(project, true)

            val runner = RunnerLayoutUiImpl(project, {}, "mybatis-sql-log", "MyBatis", "SQL Log")

            runner.addContent(
                runner.createContent(
                    "mybatis-sql-log-console",
                    SqlLogUtil.consoleView!!.component,
                    "SQL",
                    Icon.LOGO,
                    SqlLogUtil.consoleView?.component
                ).apply {
                    isCloseable = false
                }
            )

            runner.options.setLeftToolbar(DefaultActionGroup().apply {
                // run or stop button
                addAction(SqlLogAction())

                addAction(object : ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE) {
                    override fun getEditor(e: AnActionEvent) = SqlLogUtil.consoleView?.editor
                })

                addAction(ScrollToTheEndToolbarAction(SqlLogUtil.consoleView?.editor!!))

                addAction(object : AnAction("Clear All", "Clear", AllIcons.Actions.GC) {
                    override fun actionPerformed(e: AnActionEvent) {
                        SqlLogUtil.consoleView?.clear()
                    }
                })
            }, "Left")

            toolWindow.contentManager.addContent(
                toolWindow.contentManager.factory.createContent(runner.component, "Log", false)
            )
        }
    }
}
