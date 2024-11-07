package caiqichang.mybatisuite.navigation

import com.intellij.patterns.PsiJavaPatterns
import com.intellij.psi.*
import com.intellij.psi.impl.source.xml.XmlAttributeReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import com.intellij.util.xml.DomService


class JavaMethodReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(reister: PsiReferenceRegistrar) {
        reister.registerReferenceProvider(
            PsiJavaPatterns.psiElement(),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<XmlAttributeReference> {
                    println("----")
                    println(element)

                    if (element is PsiMethod) {

                        val clazz = element.containingClass
                        println(element.name)
                        println(clazz?.qualifiedName)

                        if (clazz != null) {
                            if (clazz.isInterface) {
                                val references = arrayOf<XmlAttributeReference>()
                                DomService.getInstance().getFileElements(
                                    XmlMapper::class.java,
                                    element.project,
                                    GlobalSearchScope.allScope(element.project)
                                ).forEach { xml ->
                                    if (xml.rootElement.getNamespace() == clazz.qualifiedName) {
                                        val methodName = element.name
                                        xml.rootElement.getMethod().forEach {
                                            if (it.getId().xmlAttribute?.value == methodName) {
                                                references.plus(XmlAttributeReference(it.getId().xmlAttribute!!))
                                            }
                                        }
                                    }
                                }
                                if (references.isNotEmpty()) return references
                            }
                        }
                    }
                    return arrayOf()
                }
            })
    }
}