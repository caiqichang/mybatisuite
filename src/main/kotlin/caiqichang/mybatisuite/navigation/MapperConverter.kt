package caiqichang.mybatisuite.navigation

import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
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

class EntityUsageConverter : BaseConvert<XmlAttributeValue>(), CustomReferenceConverter<XmlAttributeValue> {
    override fun createReferences(value: GenericDomValue<XmlAttributeValue>?, element: PsiElement?, context: ConvertContext?): Array<PsiReference> {
        if (context != null && element != null && element is XmlAttributeValue) {
            // use the current file namespace by default
            var namespace = MapperUtil.getNamespace(context.file)
            var entityName = element.value
            if (entityName.contains(".")) {
                // use self namespace, e.g. resultMap="com.demo.entity.MyResultMap"
                namespace = entityName.substringBeforeLast(".")
                entityName = entityName.substringAfterLast(".")
            }

            return PsiClassConverter.createJavaClassReferenceProvider(value, null, object : JavaClassReferenceProvider() {
                override fun getReferencesByString(text: String?, position: PsiElement, offsetInPosition: Int): Array<PsiReference> {
                    val defaultReference = super.getReferencesByString(text, position, offsetInPosition).toMutableList()

                    // append to java references, adapt to type like: resultMap="com.demo.entity.MyResultMap"
                    defaultReference.add(
                        object : PsiReferenceBase<PsiElement>(element, getTextRange(position), false) {
                            override fun resolve() = MapperUtil.findDefinition(context.project, namespace, entityName, (element.parent as XmlAttribute).name)
                        }
                    )

                    return defaultReference.toArray(arrayOf())
                }

                fun getTextRange(position: PsiElement) =
                    if (position.text.contains("."))
                    // only reference the last dot segment
                        TextRange.create(position.text.substring(0, position.text.lastIndexOf(".")).length + 1, position.text.length - 1)
                    else
                        ElementManipulators.getValueTextRange(position)

            }).getReferencesByElement(element)
        }
        return arrayOf()
    }
}
