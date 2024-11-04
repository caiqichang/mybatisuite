package caiqichang.mybatisuite.sqllog

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

class SqlLogConsoleFilterProvider : ConsoleFilterProvider {
    override fun getDefaultFilters(p0: Project): Array<Filter> {
        return arrayOf(SqlLogFilter())
    }
}