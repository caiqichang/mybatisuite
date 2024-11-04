package caiqichang.mybatisuite.sqllog

import com.intellij.execution.filters.Filter

class SqlLogFilter : Filter {
    
    override fun applyFilter(p0: String, p1: Int): Filter.Result? {
        println("----")
        println(p0)
        println(p1)
        println("----")
        
        return null
    }
}