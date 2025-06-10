package caiqichang.mybatisuite.sqllog

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

class SqlLogConsoleFilterProvider : ConsoleFilterProvider {
    override fun getDefaultFilters(project: Project): Array<Filter> {
        return arrayOf(Filter { line, _ ->
            SqlLogUtil.getSqlLog(project)?.handleLog(line)
            null
        })
    }
}