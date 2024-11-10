package caiqichang.mybatisuite

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.ConvertContext
import com.intellij.util.xml.Converter

class ParameterMapConverter : Converter<XmlTag>() {
    override fun toString(t: XmlTag?, context: ConvertContext?): String? {
        return null
    }

    override fun fromString(s: String?, context: ConvertContext?): XmlTag? {
        getAllXmlFiles(context?.project!!).forEach { file ->
            if (file.rootTag?.getAttribute("namespace")?.value == context.file.rootTag?.namespace) {
                file.rootTag?.children?.forEach { child ->
                    if (child is XmlTag && child.name == "resultMap") {
                        val targetAttribute = child.getAttribute("id")
                        if (targetAttribute != null && targetAttribute.value == s) {
                            return child
                        }
                    }
                }
            }
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
}