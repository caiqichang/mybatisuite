package caiqichang.mybatisuite.navigation

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.containers.toArray
import com.intellij.util.xml.*

abstract class BaseConvert<T> : ResolvingConverter<T>() {
    override fun fromString(s: String?, context: ConvertContext?): T? = null

    override fun toString(t: T?, context: ConvertContext?) = null

    override fun getVariants(context: ConvertContext?) = mutableListOf<T>()
}

class MethodConverter : BaseConvert<PsiMethod>() {
    override fun fromString(s: String?, context: ConvertContext?) =
        if (context != null)
            MapperUtil.getMethod(context.project, MapperUtil.getNamespace(context.file), s).firstOrNull()
        else
            null
}

class EntityUsageConverter : BaseConvert<XmlAttributeValue>(), CustomReferenceConverter<XmlAttributeValue> {
    override fun createReferences(value: GenericDomValue<XmlAttributeValue>?, element: PsiElement?, context: ConvertContext?): Array<PsiReference> {
        if (context != null && element != null && element is XmlAttributeValue) {
            var namespace = MapperUtil.getNamespace(context.file)
            var entityName = element.value
            if (entityName.contains(".")) {
                namespace = entityName.substring(0, entityName.lastIndexOf("."))
                entityName = getLastDotContent(entityName)
            }

            return PsiClassConverter.createJavaClassReferenceProvider(value, null, object : JavaClassReferenceProvider() {
                override fun getReferencesByString(text: String?, position: PsiElement, offsetInPosition: Int): Array<PsiReference> {
                    val defaultReference = super.getReferencesByString(text, position, offsetInPosition).toMutableList()

                    defaultReference.add(
                        object : PsiReferenceBase<PsiElement>(element, getTextRange(position), false) {
                            override fun resolve() = MapperUtil.findDefinition(context.project, namespace, entityName, (element.parent as XmlAttribute).name)

                            override fun getVariants() =
                                arrayOf(
                                    if (getElement().text.contains("."))
                                        getLastDotContent(getElement().text)
                                    else
                                        getElement().text
                                )
                        }
                    )

                    return defaultReference.toArray(arrayOf())
                }

                fun getTextRange(position: PsiElement) =
                    if (position.text.contains("."))
                        TextRange.create(position.text.substring(0, position.text.lastIndexOf(".")).length + 1, position.text.length - 1)
                    else
                        ElementManipulators.getValueTextRange(position)

            }).getReferencesByElement(element)
        }
        return arrayOf()
    }

    private fun getLastDotContent(s: String) = s.substring(s.lastIndexOf(".")).replace(".", "")
}
