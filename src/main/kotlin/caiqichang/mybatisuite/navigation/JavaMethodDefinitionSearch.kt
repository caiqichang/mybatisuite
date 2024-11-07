package caiqichang.mybatisuite.navigation

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.DefinitionsScopedSearch
import com.intellij.psi.xml.XmlAttribute
import com.intellij.util.Processor
import com.intellij.util.xml.DomService
import org.jetbrains.kotlin.j2k.getContainingClass

/**
 * navigate java mapper method to xml mapper method
 */
class JavaMethodDefinitionSearch : QueryExecutorBase<XmlAttribute, DefinitionsScopedSearch.SearchParameters>() {
    override fun processQuery(parameters: DefinitionsScopedSearch.SearchParameters, processor: Processor<in XmlAttribute>) {
        println("--1--")
        if (parameters.element is PsiMethod) {
            val clazz = parameters.element.getContainingClass()
            if (clazz != null) {
                if (clazz.isInterface) {
                    DomService.getInstance().getFileElements(
                        XmlMapper::class.java, 
                        parameters.project, 
                        GlobalSearchScope.allScope(parameters.project)
                    ).forEach { xml ->
                        if (xml.rootElement.getNamespace() == clazz.qualifiedName) {
                            val methodName = (parameters.element as PsiMethod).name
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
    }
}