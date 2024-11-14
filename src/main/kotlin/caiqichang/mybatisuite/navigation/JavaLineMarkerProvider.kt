package caiqichang.mybatisuite.navigation

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlElement
import com.intellij.util.xml.DomService
import org.jetbrains.kotlin.idea.refactoring.memberInfo.qualifiedClassNameForRendering
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass

class JavaLineMarkerProvider : BaseLineMarkerProvider() {

    private val tip = "Go to XML Mapper"

    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (!getEnable()) return
        if (element is PsiClass && element.nameIdentifier != null && element.isInterface) {
            addMarker(
                element.nameIdentifier!!,
                DomService.getInstance().getFileElements(Mapper::class.java, element.project, GlobalSearchScope.allScope(element.project))
                    .filter { it.rootElement.getNamespace().value == element.qualifiedName && it.rootElement.xmlElement != null }
                    .map { it.rootElement.xmlElement!! },
                result,
                tip
            )
        }

        if (element is PsiMethod && element.nameIdentifier != null && element.containingClass != null && element.containingClass?.isInterface == true) {
            val methods = mutableListOf<XmlElement>()
            DomService.getInstance().getFileElements(Mapper::class.java, element.project, GlobalSearchScope.allScope(element.project))
                .filter { it.rootElement.getNamespace().value == element.containingClass?.qualifiedName && it.rootElement.xmlElement != null }
                .forEach {
                    it.rootElement.getSelectList().forEach { method -> addMethod(method, element, methods) }
                    it.rootElement.getInsertList().forEach { method -> addMethod(method, element, methods) }
                    it.rootElement.getUpdateList().forEach { method -> addMethod(method, element, methods) }
                    it.rootElement.getDeleteList().forEach { method -> addMethod(method, element, methods) }
                }
            addMarker(element.nameIdentifier!!, methods, result, tip)
        }

        // kotlin support

        if (element is KtClass && element.nameIdentifier != null && element.isInterface()) {
            addMarker(
                element.nameIdentifier!!,
                DomService.getInstance().getFileElements(Mapper::class.java, element.project, GlobalSearchScope.allScope(element.project))
                    .filter { it.rootElement.getNamespace().value == element.qualifiedClassNameForRendering() && it.rootElement.xmlElement != null }
                    .map { it.rootElement.xmlElement!! },
                result,
                tip
            )
        }

        if (element is KtFunction && element.nameIdentifier != null && element.containingClass() != null && element.containingClass()?.isInterface() == true) {
            val methods = mutableListOf<XmlElement>()
            DomService.getInstance().getFileElements(Mapper::class.java, element.project, GlobalSearchScope.allScope(element.project))
                .filter { it.rootElement.getNamespace().value == element.containingClass()?.qualifiedClassNameForRendering() && it.rootElement.xmlElement != null }
                .forEach {
                    it.rootElement.getSelectList().forEach { method -> addKotlinMethod(method, element, methods) }
                    it.rootElement.getInsertList().forEach { method -> addKotlinMethod(method, element, methods) }
                    it.rootElement.getUpdateList().forEach { method -> addKotlinMethod(method, element, methods) }
                    it.rootElement.getDeleteList().forEach { method -> addKotlinMethod(method, element, methods) }
                }
            addMarker(element.nameIdentifier!!, methods, result, tip)
        }
    }

    private fun addMethod(method: Mapper.MethodIdAttr, element: PsiMethod, methods: MutableList<XmlElement>) = run {
        if (method.getId().xmlAttributeValue != null
            && method.getId().xmlAttributeValue?.value == element.name
            && method.getId().xmlElement != null
        ) methods.add(method.getId().xmlElement!!)
    }

    private fun addKotlinMethod(method: Mapper.MethodIdAttr, element: KtFunction, methods: MutableList<XmlElement>) = run {
        if (method.getId().xmlAttributeValue != null
            && method.getId().xmlAttributeValue?.value == element.name
            && method.getId().xmlElement != null
        ) methods.add(method.getId().xmlElement!!)
    }
}