package caiqichang.mybatisuite

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Font

object SqlLogUtil {

    var running = false

    private const val SQL_PREFIX = "==>  Preparing:"
    private const val PARAM_PREFIX = "==> Parameters:"

    private var sql = ""

    // types that need to add single quote
    private var stringType = listOf("String", "Date", "Time", "LocalDate", "LocalTime", "LocalDateTime", "BigDecimal", "Timestamp")

    fun handleLog(log: String) {
        if (!running) return

        // handle sql line
        if (log.startsWith(SQL_PREFIX)) {
            sql = log.replaceFirst(SQL_PREFIX, "").trim()
            return
        }

        // handle parameter line
        if (log.startsWith(PARAM_PREFIX) && sql.isNotBlank()) {
            var paramStr = log.replaceFirst(PARAM_PREFIX, "").trim()

            // remove last bracket
            if (paramStr.endsWith(")")) paramStr = paramStr.substring(0, paramStr.length - 1)

            // get parameters
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

                // get indexes of parameters
                val pos = mutableListOf<Int>()
                var idx = sql.indexOf("?")
                while (idx > 0) {
                    pos += idx
                    idx = sql.indexOf("?", idx + 1)
                }

                // fill parameters into sql
                if (pos.isNotEmpty()) {
                    var fullSql = ""
                    var lastIndex = 0
                    pos.forEachIndexed { index, it ->
                        // replace with question mark if indexes are more than parameters 
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
            showSQL(sql)
        }
    }

    var consoleView: ConsoleViewImpl? = null
    private var count = 1

    private fun showSQL(fullSql: String) {
        consoleView?.print("---- ${count++} ----\n", ConsoleViewContentType("Green", TextAttributes(JBColor.GREEN, null, null, null, Font.PLAIN)))
        consoleView?.print(fullSql, ConsoleViewContentType.NORMAL_OUTPUT)
        consoleView?.print("\n\n", ConsoleViewContentType.NORMAL_OUTPUT)

        // clear sql
        sql = ""
    }
}