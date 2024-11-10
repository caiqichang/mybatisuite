package caiqichang.mybatisuite

import com.intellij.psi.PsiMethod
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.Attribute
import com.intellij.util.xml.Convert
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.GenericAttributeValue
import com.intellij.util.xml.SubTagList

interface Mapper : DomElement {
    
    interface IdAttr : DomElement {
        @Attribute("id")
        fun id(): GenericAttributeValue<String>
    }
    
    interface MethodIdAttr : DomElement {
        @Convert(MethodIdConverter::class)
        @Attribute("id")
        fun getId(): GenericAttributeValue<String>
    }
    
    interface ResultMapAttr : DomElement {
        @Attribute("resultMap")
        fun resultMap(): GenericAttributeValue<String>
    }
    
    interface ParameterMapAttr : DomElement {
        @Convert(ParameterMapConverter::class)
        @Attribute("parameterMap")
        fun parameterMap(): GenericAttributeValue<XmlTag>
    }
    
    interface IncludeTag : DomElement {
        @Attribute("refid")
        fun refid(): GenericAttributeValue<String>
    }
    
    interface WithInclude : DomElement {
        @SubTagList("include")
        fun includeList(): List<IncludeTag>
    }
    
    interface SelectTag : MethodIdAttr, ResultMapAttr, ParameterMapAttr, WithInclude
    interface InsertTag : MethodIdAttr, ParameterMapAttr, WithInclude
    interface UpdateTag : MethodIdAttr, ParameterMapAttr, WithInclude
    interface DeleteTag : MethodIdAttr, ParameterMapAttr, WithInclude
    interface SqlTag : IdAttr, WithInclude
    interface ResultMap : IdAttr
    interface ParameterMap : IdAttr
    
    @Attribute("namespace")
    fun namespace(): GenericAttributeValue<String>
    
    @SubTagList("select")
    fun getSelects(): List<SelectTag>
    
    @SubTagList("insert")
    fun insertList(): List<InsertTag>
    
    @SubTagList("update")
    fun updateList(): List<UpdateTag>
    
    @SubTagList("delete")
    fun deleteList(): List<DeleteTag>
    
    @SubTagList("sql")
    fun sqlList(): List<SqlTag>
    
    @SubTagList("resultMap")
    fun resultMapList(): List<ResultMap>
    
    @SubTagList("parameterMap")
    fun parameterMapList(): List<ParameterMap>
}