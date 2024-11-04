package caiqichang.mybatisuite.sqllog

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class SqlLogAction : DumbAwareAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        SqlLogUtil.enable = true
    }
}