package caiqichang.mybatisuite.navigation

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

class MapperInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element.containingFile is XmlFile) {
                    val file = element.containingFile as XmlFile
                    if (file.rootTag != null && file.rootTag?.name == "mapper") {
                        if (element is XmlAttributeValue && element.parent is XmlAttribute && element.parent.parent is XmlTag) {
                            val attribute = element.parent as XmlAttribute
                            val tag = element.parent?.parent as XmlTag

                            if (attribute.name == "namespace" && tag.name == "mapper") {
                                val javaFiles = JavaPsiFacade.getInstance(element.project)
                                    .findClasses(element.value, GlobalSearchScope.allScope(element.project))
                                    .filter { it.isInterface }
                                if (javaFiles.isEmpty()) {
                                    holder.registerProblem(element, "MyBatis: Java/Kotlin interface not found", ProblemHighlightType.ERROR)
                                }
                            }

                            if (attribute.name == "id" && listOf("select", "insert", "update", "delete").contains(tag.name)) {
                                val methods = MapperUtil.getMethod(element.project, MapperUtil.getNamespace(file), element.value);
                                if (methods.isEmpty()) {
                                    holder.registerProblem(element, "MyBatis: Java/Kotlin method not found", ProblemHighlightType.ERROR)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}