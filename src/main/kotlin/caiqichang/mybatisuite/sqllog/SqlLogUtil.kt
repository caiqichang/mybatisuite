package caiqichang.mybatisuite.sqllog

object SqlLogUtil {
    
    var enable = false
    
    private val sqlPrefix = "==>  Preparing: "
    private val paramPrefix = "==> Parameters: "
    
    private var sql = ""
    
    fun handleLog(log: String) {
        if (!enable) return;
        
        // handle sql line
        if (log.startsWith(sqlPrefix)) {
            sql = log.replaceFirst(sqlPrefix, "")
            return;
        }
        
        // handle param line
        if (log.startsWith(paramPrefix) && sql.isNotBlank()) {
            
        }
        
        return;
    }
}