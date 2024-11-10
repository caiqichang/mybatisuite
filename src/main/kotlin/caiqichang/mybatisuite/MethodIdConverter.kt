package caiqichang.mybatisuite

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.xml.ConvertContext
import com.intellij.util.xml.Converter
import com.intellij.util.xml.ResolvingConverter

class MethodIdConverter : ResolvingConverter<PsiMethod>() {

    override fun toString(t: PsiMethod?, context: ConvertContext?): String? {
        return null
    }

    override fun fromString(s: String?, context: ConvertContext?): PsiMethod? {
        return getMethods(context!!, s!!).first()
    }

    override fun getVariants(context: ConvertContext?): MutableCollection<out PsiMethod> {
        return mutableListOf()
    }

    private fun getMethods(context: ConvertContext, methodName: String): List<PsiMethod> {
        val list = mutableListOf<PsiMethod>()
        val classList = JavaPsiFacade.getInstance(context.project)
            .findClasses(context.file.rootTag?.namespace!!, GlobalSearchScope.allScope(context.project))
        classList.forEach { clazz ->
            if (clazz.isInterface) {
                clazz.findMethodsByName(methodName, false).forEach { method ->
                    list.add(method)
                }
            }
        }
        return list
    }
}