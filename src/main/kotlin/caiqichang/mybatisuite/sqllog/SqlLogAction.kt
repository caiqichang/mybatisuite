package caiqichang.mybatisuite.sqllog

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SqlLogAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        SqlLogUtil.getSqlLog(event.project)?.apply {
            running = !running
            if (running) {
                SqlLogUtil.getToolWindow(event.project)?.apply {
                    activate(null)
                }
            }
        }
    }

    override fun update(event: AnActionEvent) {
        SqlLogUtil.getSqlLog(event.project)?.apply {
            event.presentation.text = "${if (running) "Stop" else "Start"} MyBatis SQL Log"
            event.presentation.icon = if (running) AllIcons.Actions.Suspend else AllIcons.Actions.Execute
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}