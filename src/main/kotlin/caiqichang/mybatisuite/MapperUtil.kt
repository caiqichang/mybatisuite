package caiqichang.mybatisuite

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
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
            getEntityAttributeValue(file.rootElement, type).forEach { attributeValue ->
                if (attributeValue.value == full
                    || (file.rootElement.getNamespace().value == namespace && attributeValue.value == entityName)
                ) {
                    list.add(attributeValue)
                }
            }
        }
        return list
    }

    private fun getEntityAttributeValue(mapper: Mapper, type: String): List<XmlAttributeValue> {
        val list = mutableListOf<XmlAttributeValue>()

        fun addInclude(includeList: List<Mapper.IncludeTag>, list: MutableList<XmlAttributeValue>) = includeList.forEach { add(it.getRefId(), list) }

        when (type) {
            "resultMap" -> {
                mapper.getSelectList().forEach { add(it.getResultMap(), list) }
            }

            "parameterMap" -> {
                mapper.getSelectList().forEach { add(it.getParameterMap(), list) }
                mapper.getInsertList().forEach { add(it.getParameterMap(), list) }
                mapper.getUpdateList().forEach { add(it.getParameterMap(), list) }
                mapper.getDeleteList().forEach { add(it.getParameterMap(), list) }
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