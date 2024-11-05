package caiqichang.mybatisuite.sqllog

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SqlLogUtil {

    var enable = false

    private val sqlPrefix = "==>  Preparing:"
    private val paramPrefix = "==> Parameters:"

    private var sql = ""

    // types that need to add single quote
    private var stringType = listOf("String", "Date", "Time", "LocalDate", "LocalTime", "LocalDateTime", "BigDecimal", "Timestamp")

    fun handleLog(log: String) {
        if (!enable) return;

        // handle sql line
        if (log.startsWith(sqlPrefix)) {
            sql = log.replaceFirst(sqlPrefix, "").trim()
            return;
        }

        // handle param line
        if (log.startsWith(paramPrefix) && sql.isNotBlank()) {
            var paramStr = log.replaceFirst(paramPrefix, "").trim()

            // remove last bracket
            if (paramStr.endsWith(")")) paramStr = paramStr.substring(0, paramStr.length - 1)

            if (paramStr.isNotBlank()) {
                val paramList = paramStr.split("), ").map {
                    // get value and type
                    var value = it.substring(0, it.lastIndexOf("("))
                    val type = it.substring(it.lastIndexOf("(")).replaceFirst("(", "")

                    if (stringType.contains(type)) {
                        // escape single quote and add single quote to both sides
                        value = "'${value.replace("'", "''")}'"
                    }

                    return@map value
                }

                val pos = mutableListOf<Int>()
                var idx = sql.indexOf("?")
                while (idx > 0) {
                    pos += idx
                    idx = sql.indexOf("?", idx + 1)
                }

                if (pos.isNotEmpty()) {
                    var fullSql = ""
                    var lastIndex = 0
                    pos.forEachIndexed { index, it ->
                        fullSql += "${sql.substring(lastIndex, it)}${if (paramList.size > index) paramList[index] else "?"}"
                        lastIndex = it + 1
                    }
                    if (sql.length > lastIndex) {
                        fullSql += sql.substring(lastIndex, sql.length)
                    }

                    showSQL(fullSql)
                    return
                }
            }

            // sql only, no param
            showSQL("" + sql)
        }
    }

    private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn")

    private fun showSQL(fullSql: String) {
        // clear sql
        sql = ""

        println("---- ${format.format(LocalDateTime.now())} ----")
        println(fullSql)
        println()

    }
}