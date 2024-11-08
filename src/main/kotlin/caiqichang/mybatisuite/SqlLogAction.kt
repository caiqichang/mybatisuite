package caiqichang.mybatisuite

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SqlLogAction : AnAction() {

    private var init = false
    private var running = false

    override fun actionPerformed(event: AnActionEvent) {
        if (!init && event.project != null) {
            SqlLogUtil.init(event.project!!)
            init = true
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