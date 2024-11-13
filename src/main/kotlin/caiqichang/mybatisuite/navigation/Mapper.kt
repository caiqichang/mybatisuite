package caiqichang.mybatisuite.navigation

import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.xml.*

// properties must be getXXX()

interface Mapper : DomElement {

    interface IdAttr : DomElement {
        @NameValue
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
        fun getResultMap(): GenericAttributeValue<XmlAttributeValue>
    }

    interface ParameterMapAttr : DomElement {
        @Convert(EntityUsageConverter::class)
        @Attribute("parameterMap")
        fun getParameterMap(): GenericAttributeValue<XmlAttributeValue>
    }

    interface IncludeTag : DomElement {
        @Convert(EntityUsageConverter::class)
        @Attribute("refid")
        fun getRefId(): GenericAttributeValue<XmlAttributeValue>
    }

    interface ForeachTag : WithInclude
    interface IfTag : WithInclude
    interface WhenTag : WithInclude
    interface OtherwiseTag : WithInclude
    interface WhereTag : WithInclude
    interface SetTag : WithInclude
    interface TrimTag : WithInclude
    interface SelectKeyTag : WithInclude

    interface WithInclude : DomElement {
        @SubTagList("include")
        fun getIncludeList(): List<IncludeTag>

        @SubTagList("foreach")
        fun getForeachList(): List<ForeachTag>

        @SubTagList("if")
        fun getIfList(): List<IfTag>

        @SubTagList("when")
        fun getWhenList(): List<WhenTag>

        @SubTagList("otherwise")
        fun getOtherwiseList(): List<OtherwiseTag>

        @SubTagList("where")
        fun getWhereList(): List<WhereTag>

        @SubTagList("set")
        fun getSetList(): List<SetTag>

        @SubTagList("trim")
        fun getTrimList(): List<TrimTag>

        @SubTagList("selectKey")
        fun getSelectKeyList(): List<SelectKeyTag>
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