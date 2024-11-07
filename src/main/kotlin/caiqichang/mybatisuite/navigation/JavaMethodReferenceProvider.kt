package caiqichang.mybatisuite.navigation

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import com.intellij.util.xml.DomService
import org.jetbrains.kotlin.j2k.getContainingClass

class JavaMethodReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        
        println("---")
        
        if (element is PsiMethod) {
            val clazz = element.containingClass
            if (clazz != null) {
                if (clazz.isInterface) {
                    DomService.getInstance().getFileElements(
                        XmlMapper::class.java, 
                        element.project, 
                        GlobalSearchScope.allScope(element.project)
                    ).forEach { xml ->
                        if (xml.rootElement.getNamespace() == clazz.qualifiedName) {
                            val methodName = element.name
                            xml.rootElement.getMethod().forEach { 
                                if (it.getId() == methodName) {
//                                    processor.process(it.getId())
                                }
                            }
                        }
                    }
                }
            }
        }
        return arrayOf()
    }
}