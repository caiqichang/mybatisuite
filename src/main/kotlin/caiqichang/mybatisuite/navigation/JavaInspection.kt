package caiqichang.mybatisuite.navigation

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlElement
import com.intellij.util.xml.DomService
import org.jetbrains.kotlin.idea.refactoring.memberInfo.qualifiedClassNameForRendering
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass

class JavaInspection : LocalInspectionTool() {
    
    private val tip = "MyBatis: XML method not found"
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (!MapperUtil.enableXmlMapperResolving()) return
                if (element is PsiMethod
                    && element.nameIdentifier != null
                    && element.containingClass != null
                    && element.containingClass?.isInterface == true
                ) {
                    val xmlFiles = DomService.getInstance()
                        .getFileElements(Mapper::class.java, element.project, GlobalSearchScope.allScope(element.project))
                        .filter { 
                            it.rootElement.getNamespace().value == element.containingClass?.qualifiedName 
                                    && it.rootElement.xmlElement != null 
                        }

                    if (xmlFiles.isNotEmpty()) {
                        val methods = mutableListOf<XmlElement>()
                        xmlFiles.forEach {
                            it.rootElement.getMethodList().forEach { method ->
                                if (method.getId().xmlAttributeValue != null
                                    && method.getId().xmlAttributeValue?.value == element.name
                                    && method.getId().xmlElement != null
                                ) methods.add(method.getId().xmlElement!!)
                            }
                        }
                        if (methods.isEmpty()) {
                            holder.registerProblem(element.nameIdentifier!!, tip, ProblemHighlightType.ERROR)
                        }
                    }
                }

                // kotlin support

                if (element is KtFunction
                    && element.nameIdentifier != null
                    && element.containingClass() != null
                    && element.containingClass()?.isInterface() == true
                ) {
                    val xmlFiles = DomService.getInstance()
                        .getFileElements(Mapper::class.java, element.project, GlobalSearchScope.allScope(element.project))
                        .filter { 
                            it.rootElement.getNamespace().value == element.containingClass()?.qualifiedClassNameForRendering() 
                                    && it.rootElement.xmlElement != null 
                        }

                    if (xmlFiles.isNotEmpty()) {
                        val methods = mutableListOf<XmlElement>()
                        xmlFiles.forEach {
                            it.rootElement.getMethodList().forEach { method ->
                                if (method.getId().xmlAttributeValue != null
                                    && method.getId().xmlAttributeValue?.value == element.name
                                    && method.getId().xmlElement != null
                                ) methods.add(method.getId().xmlElement!!)
                            }
                        }
                        if (methods.isEmpty()) {
                            holder.registerProblem(element.nameIdentifier!!, tip, ProblemHighlightType.ERROR)
                        }
                    }
                }
            }
        }
    }
}