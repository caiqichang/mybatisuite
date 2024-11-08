package caiqichang.mybatisuite

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext
import com.intellij.util.containers.toArray

class XmlReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(register: PsiReferenceRegistrar) {
        register.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue(),
            object : PsiReferenceProvider() {

                private val METHOD_TAG = listOf("select", "insert", "update", "delete")

                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    if (element is XmlAttributeValue) {
                        val attribute = element.parent
                        if (attribute is XmlAttribute) {
                            if (attribute.parent is XmlTag) {
                                if (METHOD_TAG.contains(attribute.parent.name)) {
                                    val namespace = getNamespace(attribute)
                                    if (namespace != null) {
                                        val list = mutableListOf<PsiReference>()
                                        val classList = JavaPsiFacade.getInstance(element.project).findClasses(namespace, GlobalSearchScope.allScope(element.project))
                                        classList.forEach { clazz ->
                                            clazz.findMethodsByName(element.value, false).forEach { method ->
                                                list.add(object : PsiReferenceBase<XmlAttributeValue>(element) {
                                                    override fun resolve(): PsiElement? {
                                                        return method
                                                    }

                                                    override fun getRangeInElement(): TextRange {
                                                        val offset = getElement().text.indexOf(element.value)
                                                        return TextRange(offset, offset + element.value.length)
                                                    }
                                                })
                                            }
                                        }
                                        return list.toArray(arrayOf())
                                    }
                                }
                            }
                        }
                    }
                    return PsiReference.EMPTY_ARRAY
                }

                private fun getNamespace(element: XmlAttribute): String? {
                    val root = getRoot(element)
                    if (root != null && root.name == "mapper") {
                        return root.getAttribute("namespace")?.value
                    }
                    return null
                }

                private fun getRoot(element: XmlAttribute): XmlTag? {
                    var current: PsiElement? = element.parent
                    while (current != null) {
                        if (current is XmlTag) {
                            if (current.parentTag == null) {
                                return current
                            } else {
                                current = current.parentTag
                            }
                        }
                    }
                    return null
                }
            }
        )
    }
}