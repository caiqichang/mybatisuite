package caiqichang.mybatisuite.sqllog

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

class SqlLogConsoleFilterProvider : ConsoleFilterProvider {
    override fun getDefaultFilters(project: Project): Array<Filter> {
        return arrayOf(object : Filter {
            override fun applyFilter(line: String, index: Int): Filter.Result? {
                SqlLogUtil.getLog(project)?.handleLog(line)
                return null
            }
        })
    }
}