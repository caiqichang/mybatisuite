package caiqichang.mybatisuite

import caiqichang.mybatisuite.XmlReferenceContributor.TargetData
import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.DefinitionsScopedSearch
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.Processor
import com.intellij.util.containers.toArray

class XmlDefinitionScopedSearch : QueryExecutorBase<XmlElement, DefinitionsScopedSearch.SearchParameters>(true) {

    override fun processQuery(query: DefinitionsScopedSearch.SearchParameters, consumer: Processor<in XmlElement>) {
        println("----")
        
//        val element = query.element
//        if (element is XmlAttributeValue) {
//            val attribute = element.parent
//            if (attribute is XmlAttribute) {
//                // check mapper with namespace
//                val namespace = getNamespace(attribute)
//                if (!namespace.isNullOrEmpty()) {
//                    val tag = attribute.parent
//                    if (tag is XmlTag) {
//                        getReferences(element, element, attribute, tag, namespace, consumer)
//                    }
//                }
//            }
//        }
    }

    class TargetData(
        val targetNamespace: String,
        val targetName: String,
    )

    private val METHOD_TAG = listOf("select", "insert", "update", "delete")
    private val ENTITY_ATTRIBUTE = listOf("parameterMap", "resultMap")

    private fun getReferences(element: PsiElement, attributeValue: XmlAttributeValue, attribute: XmlAttribute, tag: XmlTag, namespace: String, consumer: Processor<in PsiElement>): Array<PsiReference> {
        if (METHOD_TAG.contains(tag.name)) {

            // reference java/kotlin method with xml mapper
            if (attribute.name == "id") {
                val list = mutableListOf<PsiReference>()
                val classList = JavaPsiFacade.getInstance(element.project).findClasses(namespace, GlobalSearchScope.allScope(element.project))
                classList.forEach { clazz ->
                    if (clazz.isInterface) {
                        clazz.findMethodsByName(attributeValue.value, false).forEach { method ->
                            list.add(createReference(attributeValue, method))
                            consumer.process(method)
                        }
                    }
                }
                return list.toArray(arrayOf())
            }

            // reference like <select resultMap="">
            if (ENTITY_ATTRIBUTE.contains(attribute.name)) {
                val targetData = getTargetData(attributeValue, namespace)
                if (targetData != null) {
                    val list = mutableListOf<PsiReference>()
                    getAllXmlFiles(element.project).forEach { file ->

                        // find like <resultMap id="">
                        if (file.rootTag?.getAttribute("namespace")?.value == targetData.targetNamespace) {
                            file.rootTag?.children?.forEach { child ->
                                if (child is XmlTag && child.name == attribute.name) {
                                    val targetAttribute = child.getAttribute("id")
                                    if (targetAttribute != null && targetAttribute.value == targetData.targetName) {
                                        list.add(createReference(attributeValue, targetAttribute.valueElement!!))
                                        consumer.process(targetAttribute.valueElement!!)
                                    }
                                }
                            }
                        }

                    }
                    return list.toArray(arrayOf())
                }
            }
        }

        // reference like <resultMap id="">
        if (ENTITY_ATTRIBUTE.contains(tag.name) && attribute.name == "id") {
            val targetData = getTargetData(attributeValue, namespace)
            if (targetData != null) {
                val list = mutableListOf<PsiReference>()
                getAllXmlFiles(element.project).forEach { file ->

                    // find like <resultMap id="">
                    file.rootTag?.children?.forEach { child ->
                        if (child is XmlTag) {
                            child.attributes.forEach { attr ->
                                if (attr.name == tag.name
                                    && ((file.rootTag?.getAttribute("namespace")?.value == namespace && attr.value == targetData.targetName)
                                            || attr.value == "${namespace}.${targetData.targetName}")
                                ) {
                                    list.add(createReference(attributeValue, attr.valueElement!!))
                                }
                            }
                        }
                    }

                }
                return list.toArray(arrayOf())
            }
        }

        // reference like <include refid="">
        if (tag.name == "include" && attribute.name == "refid") {
            val targetData = getTargetData(attributeValue, namespace)
            if (targetData != null) {
                val list = mutableListOf<PsiReference>()
                getAllXmlFiles(element.project).forEach { file ->

                    // find like <sql id="">
                    if (file.rootTag?.getAttribute("namespace")?.value == targetData.targetNamespace) {
                        file.rootTag?.children?.forEach { child ->
                            if (child is XmlTag && child.name == "sql") {
                                val targetAttribute = child.getAttribute("id")
                                if (targetAttribute != null && targetAttribute.value == targetData.targetName) {
                                    list.add(createReference(attributeValue, targetAttribute.valueElement!!))
                                    consumer.process(targetAttribute.valueElement!!)
                                }
                            }
                        }
                    }

                }
                return list.toArray(arrayOf())
            }
        }

        // reference like <sql id="">
        if (tag.name == "sql" && attribute.name == "id") {
            val targetData = getTargetData(attributeValue, namespace)
            if (targetData != null) {
                val list = mutableListOf<PsiReference>()
                getAllXmlFiles(element.project).forEach { file ->

                    // find like <include refid="">
                    if (file.rootTag != null) {
                        val stack = ArrayDeque<XmlTag>()
                        stack.addFirst(file.rootTag!!)
                        while (stack.isNotEmpty()) {
                            val currentTag = stack.removeFirst()
                            if (
                                (file.rootTag?.getAttribute("namespace")?.value == namespace && currentTag.getAttribute("refid")?.value == targetData.targetName)
                                || currentTag.getAttribute("refid")?.value == "${namespace}.${targetData.targetName}"
                            ) {
                                list.add(createReference(attributeValue, currentTag.getAttribute("refid")?.valueElement!!))
                            }
                            currentTag.subTags.forEach { subTag ->
                                stack.addFirst(subTag)
                            }
                        }
                    }

                }
                return list.toArray(arrayOf())
            }
        }

        return PsiReference.EMPTY_ARRAY
    }

    private fun getTargetData(attributeValue: XmlAttributeValue, namespace: String): TargetData? {
        val fullValue = if (attributeValue.value.contains(".")) attributeValue.value else "${namespace}.${attributeValue.value}"
        val targetName = fullValue.substring(fullValue.lastIndexOf(".")).replace(".", "")
        val targetNamespace = fullValue.substring(0, fullValue.lastIndexOf("."))
        if (targetName.isNotEmpty() && targetNamespace.isNotEmpty()) {
            return TargetData(targetNamespace, targetName)
        }
        return null
    }

    private fun getAllXmlFiles(project: Project): List<XmlFile> {
        val xmlFiles = mutableListOf<XmlFile>()
        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
        ProjectRootManager.getInstance(project).contentRoots.forEach { root ->
            VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Void?>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (!fileIndex.isExcluded(file) && file.name.endsWith(".xml")) {
                        val psiFile = PsiManager.getInstance(project).findFile(file)
                        if (psiFile is XmlFile && psiFile.rootTag?.name == "mapper") {
                            xmlFiles.add(psiFile)
                        }
                    }
                    return true
                }
            })
        }
        return xmlFiles
    }

    private fun createReference(attributeValue: XmlAttributeValue, element: PsiElement) = object : PsiReferenceBase<XmlAttributeValue>(attributeValue) {
        override fun resolve(): PsiElement {
            return element
        }

        override fun getRangeInElement(): TextRange {
            val offset = getElement().text.indexOf(attributeValue.value)
            return TextRange(offset, offset + attributeValue.value.length)
        }
    }

    private fun getNamespace(element: XmlAttribute): String? {
        var current: PsiElement? = element.parent
        while (current != null) {
            if (current is XmlTag) {
                if (current.parentTag == null) {
                    if (current.name == "mapper") {
                        return current.getAttribute("namespace")?.value
                    }
                } else {
                    current = current.parentTag
                }
            }
        }
        return null
    }
}