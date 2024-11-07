package caiqichang.mybatisuite.navigation

import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.SubTagsList
import com.intellij.util.xmlb.annotations.Attribute

/**
 * XML Mapper type
 */
interface XmlMapper : DomElement {
    
    @Attribute("namespace")
    fun getNamespace(): String

    interface IdElement : DomElement {
        @Attribute("id")
        fun getId(): String
    }

    interface Method : IdElement
    interface Sql : IdElement
    interface ParameterMap : IdElement
    interface ResultMap : IdElement

    @SubTagsList("insert", "update", "delete", "select")
    fun getMethod(): List<Method>

    @SubTagsList("sql")
    fun getSql(): List<Sql>

    @SubTagsList("parameterMap")
    fun getParameterMap(): List<ParameterMap>

    @SubTagsList("resultMap")
    fun getResultMap(): List<ResultMap>
}