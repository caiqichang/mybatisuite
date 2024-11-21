package caiqichang.mybatisuite.navigation

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

class MapperLineMarkerProvider : BaseLineMarkerProvider() {

    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (!MapperUtil.enableXmlMapperResolving()) return
        if (element.containingFile is XmlFile) {
            val file = element.containingFile as XmlFile
            if (file.rootTag != null && file.rootTag?.name == "mapper") {
                if (element is XmlAttributeValue && element.parent is XmlAttribute && element.parent.parent is XmlTag) {
                    val attribute = element.parent as XmlAttribute
                    val tag = element.parent?.parent as XmlTag

                    if (attribute.name == "namespace" && tag.name == "mapper") {
                        addMarker(
                            element,
                            JavaPsiFacade.getInstance(element.project)
                                .findClasses(element.value, GlobalSearchScope.allScope(element.project))
                                .filter { it.isInterface },
                            result,
                            "Go to Interface"
                        )
                    }

                    if (attribute.name == "id" && listOf("select", "insert", "update", "delete").contains(tag.name)) {
                        addMarker(
                            element,
                            MapperUtil.getMethod(element.project, MapperUtil.getNamespace(file), element.value),
                            result,
                            "Go to Method"
                        )
                    }
                }
            }
        }
    }
}