package caiqichang.mybatisuite.navigation

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomService

object MapperUtil {
    fun getNamespace(xmlFile: XmlFile) = xmlFile.rootTag?.getAttributeValue("namespace") ?: ""

    fun getMethod(project: Project, namespace: String?, methodName: String?): List<PsiMethod> {
        val list = mutableListOf<PsiMethod>()
        if (namespace.isNullOrBlank() || methodName.isNullOrBlank()) return list
        JavaPsiFacade.getInstance(project).findClasses(namespace, GlobalSearchScope.allScope(project))
            .filter { it.isInterface }
            .forEach {
                it.findMethodsByName(methodName, false)
                    .forEach { method -> list.add(method) }
            }
        return list
    }

    fun findDefinition(project: Project, namespace: String?, entityName: String?, type: String?): XmlAttributeValue? {
        if (namespace.isNullOrBlank() || entityName.isNullOrBlank() || type.isNullOrBlank()) return null
        val list = mutableListOf<XmlAttributeValue>()
        DomService.getInstance().getFileElements(Mapper::class.java, project, GlobalSearchScope.allScope(project))
            .filter { it.rootElement.getNamespace().value == namespace }
            .forEach { file ->
                when (type) {
                    "refid" -> file.rootElement.getSqlList().forEach { addDefine(entityName, it, list) }
                    "resultMap" -> file.rootElement.getResultMapList().forEach { addDefine(entityName, it, list) }
                    "parameterMap" -> file.rootElement.getParameterMapList().forEach { addDefine(entityName, it, list) }
                }
            }
        return list.firstOrNull()
    }

    private fun addDefine(target: String, id: Mapper.IdAttr, list: MutableList<XmlAttributeValue>) {
        if (id.getId().xmlAttributeValue != null && id.getId().xmlAttributeValue?.value == target) {
            list.add(id.getId().xmlAttributeValue!!)
        }
    }
}