package caiqichang.mybatisuite

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomService
import com.intellij.util.xml.GenericAttributeValue

object MapperUtil {

    fun getNamespace(xmlFile: XmlFile) = xmlFile.rootTag?.getAttributeValue("namespace") ?: ""

    fun getMethod(project: Project, namespace: String?, methodName: String?): List<PsiMethod> {
        val list = mutableListOf<PsiMethod>()
        if (namespace.isNullOrBlank() || methodName.isNullOrBlank()) return list
        val classList = JavaPsiFacade.getInstance(project).findClasses(namespace, GlobalSearchScope.allScope(project))
        classList.forEach { clazz ->
            if (clazz.isInterface) {
                clazz.findMethodsByName(methodName, false).forEach { method ->
                    list.add(method)
                }
            }
        }
        return list
    }
    
    fun findDefinition(project: Project, namespace: String?, entityName: String?, type: String?): XmlAttributeValue? {
        if (namespace.isNullOrBlank() || entityName.isNullOrBlank() || type.isNullOrBlank()) return null
        val mapperFiles = DomService.getInstance().getFileElements(Mapper::class.java, project, GlobalSearchScope.allScope(project))
        val list = mutableListOf<XmlAttributeValue>()
        mapperFiles.filter { it.rootElement.getNamespace().value == namespace }
            .forEach { file ->
                when (type) {
                    "refid" -> {
                        file.rootElement.getSqlList().forEach { addDefine(entityName, it, list) }
                    }
                    "resultMap" -> {
                        file.rootElement.getResultMapList().forEach { addDefine(entityName, it, list) }
                    }
                    "parameterMap" -> {
                        file.rootElement.getParameterMapList().forEach { addDefine(entityName, it, list) }
                    }
                }
            }
        return list.firstOrNull()
    }
    
    private fun addDefine(target: String, id: Mapper.IdAttr, list: MutableList<XmlAttributeValue>) {
        if (id.getId().xmlAttributeValue != null && id.getId().xmlAttributeValue?.value == target) {
            list.add(id.getId().xmlAttributeValue!!)
        }
    }
    
    fun getEntity(project: Project, namespace: String?, entityName: String?, type: String?): List<XmlAttributeValue> {
        val list = mutableListOf<XmlAttributeValue>()
        if (namespace.isNullOrBlank() || entityName.isNullOrBlank() || type.isNullOrBlank()) return list
        val mapperFiles = DomService.getInstance().getFileElements(Mapper::class.java, project, GlobalSearchScope.allScope(project))
        mapperFiles.filter { it.rootElement.getNamespace().value == namespace }
            .forEach { file ->
                getUsageAttributeValue(file.rootElement, type).forEach { attributeValue ->
                    if (attributeValue.value == entityName) {
                        list.add(attributeValue)
                    }
                }
            }
        return list
    }

    private fun getUsageAttributeValue(mapper: Mapper, type: String): List<XmlAttributeValue> {
        val list = mutableListOf<XmlAttributeValue>()
        when (type) {
            "resultMap" -> {
                mapper.getResultMapList().forEach { add(it.getId(), list) }
            }

            "parameterMap" -> {
                mapper.getParameterMapList().forEach { add(it.getId(), list) }
            }

            "refid" -> {
                mapper.getSqlList().forEach { add(it.getId(), list) }
            }
        }
        return list
    }

    private fun add(it: GenericAttributeValue<String>, list: MutableList<XmlAttributeValue>) {
        val i = it.xmlAttributeValue
        if (i != null) {
            list.add(i)
        }
    }

    fun getEntityUsage(project: Project, namespace: String?, entityName: String?, type: String?): List<XmlAttributeValue> {
        val list = mutableListOf<XmlAttributeValue>()
        if (namespace.isNullOrBlank() || entityName.isNullOrBlank() || type.isNullOrBlank()) return list
        val mapperFiles = DomService.getInstance().getFileElements(Mapper::class.java, project, GlobalSearchScope.allScope(project))
        val full = "$namespace.$entityName"
        mapperFiles.forEach { file ->
            getEntityAttributeValue(file.rootElement, type).forEach { attribute ->
                if ((attribute.value == full || (file.rootElement.getNamespace().value == namespace && attribute.value == entityName))) {
                    list.add(attribute)
                }
            }
        }
        return list
    }
    
    private fun addTag(it: GenericAttributeValue<XmlAttributeValue>, list: MutableList<XmlAttributeValue>) {
        val i = it.xmlAttributeValue
        if (i != null) {
            list.add(i)
        }
    }

    private fun getEntityAttributeValue(mapper: Mapper, type: String): List<XmlAttributeValue> {
        val list = mutableListOf<XmlAttributeValue>()

        fun addInclude(includeList: List<Mapper.IncludeTag>, list: MutableList<XmlAttributeValue>) = includeList.forEach { addTag(it.getRefId(), list) }

        when (type) {
            "resultMap" -> {
                mapper.getSelectList().forEach { addTag(it.getResultMap(), list) }
            }

            "parameterMap" -> {
                mapper.getSelectList().forEach { addTag(it.getParameterMap(), list) }
                mapper.getInsertList().forEach { addTag(it.getParameterMap(), list) }
                mapper.getUpdateList().forEach { addTag(it.getParameterMap(), list) }
                mapper.getDeleteList().forEach { addTag(it.getParameterMap(), list) }
            }

            "sql" -> {
                mapper.getSelectList().forEach { include ->
                    addInclude(include.getIncludeList(), list)
                }
                mapper.getInsertList().forEach { include ->
                    addInclude(include.getIncludeList(), list)
                }
                mapper.getUpdateList().forEach { include ->
                    addInclude(include.getIncludeList(), list)
                }
                mapper.getDeleteList().forEach { include ->
                    addInclude(include.getIncludeList(), list)
                }
            }
        }
        return list
    }
}