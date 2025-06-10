package caiqichang.mybatisuite.navigation

import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.xml.*

// properties must be getXXX()

interface Mapper : DomElement {

    interface IdAttr : DomElement {
        // @NameValue set this as a declaration
        @NameValue
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

    interface SelectAttr : DomElement {
        @Convert(EntityUsageConverter::class)
        @Attribute("select")
        fun getSelect(): GenericAttributeValue<XmlAttributeValue>
    }

    interface AssociationTag : SelectAttr
    interface CollectionTag : SelectAttr

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

    // tags that with include <include>, recursive
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

    interface MethodTag : IdAttr, ResultMapAttr, ParameterMapAttr, WithInclude
    interface SelectTag : MethodTag
    interface InsertTag : MethodTag
    interface UpdateTag : MethodTag
    interface DeleteTag : MethodTag
    interface SqlTag : IdAttr, WithInclude
    interface ResultMap : IdAttr {
        @SubTagList("association")
        fun getAssociationList(): List<AssociationTag>

        @SubTagList("collection")
        fun getCollectionList(): List<CollectionTag>
    }

    interface ParameterMap : IdAttr


    @Attribute("namespace")
    fun getNamespace(): GenericAttributeValue<String>


    @SubTagsList("select", "insert", "update", "delete")
    fun getMethodList(): List<MethodTag>

    // these method tags also need to be defined

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