package caiqichang.mybatisuite.sqllog

import com.intellij.execution.filters.Filter

class SqlLogFilter : Filter {

    override fun applyFilter(p0: String, p1: Int): Filter.Result? {
        SqlLogUtil.handleLog(p0)
        return null
    }
}