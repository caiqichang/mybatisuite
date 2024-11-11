package caiqichang.mybatisuite

import com.intellij.util.xml.*

// properties must be getXXX()

interface Mapper : DomElement {

    interface IdAttr : DomElement {
        @Convert(EntityConverter::class)
        @Attribute("id")
        fun getId(): GenericAttributeValue<String>
    }

    interface MethodIdAttr : DomElement {
        @Convert(MethodConverter::class)
        @Attribute("id")
        fun getId(): GenericAttributeValue<String>
    }

    interface ResultMapAttr : DomElement {
        @Convert(EntityUsageConverter::class)
        @Attribute("resultMap")
        fun getResultMap(): GenericAttributeValue<String>
    }

    interface ParameterMapAttr : DomElement {
        @Convert(EntityUsageConverter::class)
        @Attribute("parameterMap")
        fun getParameterMap(): GenericAttributeValue<String>
    }

    interface IncludeTag : DomElement {
        @Convert(EntityUsageConverter::class)
        @Attribute("refid")
        fun getRefId(): GenericAttributeValue<String>
    }

    interface WithInclude : DomElement {
        @SubTagList("include")
        fun getIncludeList(): List<IncludeTag>
    }

    interface SelectTag : MethodIdAttr, ResultMapAttr, ParameterMapAttr, WithInclude
    interface InsertTag : MethodIdAttr, ParameterMapAttr, WithInclude
    interface UpdateTag : MethodIdAttr, ParameterMapAttr, WithInclude
    interface DeleteTag : MethodIdAttr, ParameterMapAttr, WithInclude
    interface SqlTag : IdAttr, WithInclude
    interface ResultMap : IdAttr
    interface ParameterMap : IdAttr

    @Attribute("namespace")
    fun getNamespace(): GenericAttributeValue<String>

    @SubTagList("select")
    fun getSelectList(): List<SelectTag>

    @SubTagList("insert")
    fun getInsertList(): List<InsertTag>

    @SubTagList("update")
    fun getUpdateList(): List<UpdateTag>

    @SubTagList("delete")
    fun getDeleteList(): List<DeleteTag>

    @SubTagList("sql")
    fun getSqlList(): List<SqlTag>

    @SubTagList("resultMap")
    fun getResultMapList(): List<ResultMap>

    @SubTagList("parameterMap")
    fun getParameterMapList(): List<ParameterMap>
}