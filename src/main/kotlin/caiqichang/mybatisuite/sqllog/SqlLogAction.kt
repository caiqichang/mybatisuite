package caiqichang.mybatisuite.sqllog

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SqlLogAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        SqlLogUtil.getLog(event.project)?.apply {
            running = !running
        }
    }

    override fun update(event: AnActionEvent) {
        SqlLogUtil.getLog(event.project)?.apply {
            event.presentation.text = "${if (running) "Stop" else "Start"} MyBatis SQL Log"
            event.presentation.icon = if (running) AllIcons.Actions.Suspend else AllIcons.Actions.Execute
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}