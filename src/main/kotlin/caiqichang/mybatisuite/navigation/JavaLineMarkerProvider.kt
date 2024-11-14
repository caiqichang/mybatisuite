package caiqichang.mybatisuite.navigation

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.xml.DomService

class JavaLineMarkerProvider : BaseLineMarkerProvider() {

    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (!getEnable()) return
        if (element.containingFile is PsiJavaFile) {
            val file = element.containingFile as PsiJavaFile
            if (element is PsiClass && element.isInterface) {
                addMarker(
                    element,
                    DomService.getInstance().getFileElements(Mapper::class.java, element.project, GlobalSearchScope.allScope(element.project))
                        .filter { it.rootElement.getNamespace().value == file.packageName && it.rootElement.xmlElement != null }
                        .map { it.rootElement.xmlElement!! },
                    result,
                )
            }

            if (element is PsiMethod && element.containingClass != null && element.containingClass?.isInterface == true) {
                
            }
        }
    }
}