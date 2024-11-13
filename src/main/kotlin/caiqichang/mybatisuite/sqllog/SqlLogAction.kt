package caiqichang.mybatisuite.sqllog

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SqlLogAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        SqlLogUtil.running = !SqlLogUtil.running
    }

    override fun update(e: AnActionEvent) {
        e.presentation.text = "${if (SqlLogUtil.running) "Stop" else "Start"} MyBatis SQL Log"
        e.presentation.icon = if (SqlLogUtil.running) AllIcons.Actions.Suspend else AllIcons.Actions.Execute
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}