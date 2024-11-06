package caiqichang.mybatisuite.sqllog

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SqlLogAction : AnAction() {

    private var inited = false
    private var running = false

    override fun actionPerformed(e: AnActionEvent) {
        if (!inited && e.project != null) {
            SqlLogUtil.init(e.project!!)
            inited = true
        }

        // switch running status
        running = !running
        SqlLogUtil.running = running
    }

    override fun update(e: AnActionEvent) {
        e.presentation.text = "${if (running) "Stop" else "Start"} MyBatis SQL Log"
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}