package caiqichang.mybatisuite

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.util.containers.toArray
import com.intellij.util.xml.*

abstract class BaseConvert<T> : ResolvingConverter<T>() {
    override fun toString(t: T?, context: ConvertContext?): String? {
        return null
    }

    override fun getVariants(context: ConvertContext?): MutableCollection<out T> {
        return mutableListOf()
    }
}

class MethodConverter : BaseConvert<PsiMethod>() {
    override fun fromString(s: String?, context: ConvertContext?): PsiMethod? {
        if (context != null) {
            return MapperUtil.getMethod(context.project, MapperUtil.getNamespace(context.file), s).firstOrNull()
        }
        return null
    }
}

class EntityConverter : BaseConvert<XmlAttributeValue>(), CustomReferenceConverter<XmlAttributeValue> {
    override fun fromString(s: String?, context: ConvertContext?): XmlAttributeValue? {
//        if (context != null
//            && !s.isNullOrBlank()
//            && context.xmlElement != null
//            && context.xmlElement?.parent != null
//            && context.xmlElement?.parent is XmlTag
//        ) {
//            return MapperUtil.getEntity(context.project, MapperUtil.getNamespace(context.file), s, (context.xmlElement?.parent as XmlTag).name).firstOrNull()
//        }
        return null
    }

    override fun createReferences(value: GenericDomValue<XmlAttributeValue>?, element: PsiElement?, context: ConvertContext?): Array<PsiReference> {
        if (element != null
            && context != null
            && value != null
            && value is GenericAttributeValue
            && value.xmlAttributeValue != null
            && value.xmlAttributeValue?.value != null
        ) {
            return PsiClassConverter.createJavaClassReferenceProvider(value, null, object : JavaClassReferenceProvider() {

                override fun getReferencesByString(text: String?, position: PsiElement, offsetInPosition: Int): Array<PsiReference> {
                    val defaultReference = super.getReferencesByString(text, position, offsetInPosition).toMutableList()
                    MapperUtil.getEntityUsage(
                        context.project,
                        MapperUtil.getNamespace(context.file),
                        value.xmlAttributeValue?.value,
                        (element.parent?.parent as XmlTag).name
                    ).map {
                        object : PsiReferenceBase<PsiElement>(element, getTextRange(position), false) {

                            override fun resolve(): PsiElement {
                                return it
                            }

                            override fun getVariants(): Array<Any> {
                                var str = getElement().text
                                if (str.contains(".")) {
                                    str = str.substring(str.indexOf(".")).replace(".", "")
                                }
                                return arrayOf(str)
                            }
                        }
                    }.forEach {
                        defaultReference.add(it)
                    }
                    return defaultReference.toArray(arrayOf())
                }

                fun getTextRange(position: PsiElement): TextRange {
                    val t = position.text
                    return if (t.contains(".")) {
                        TextRange.create(t.substring(0, t.lastIndexOf(".")).length + 1, t.length - 1)
                    } else {
                        ElementManipulators.getValueTextRange(position)
                    }
                }
            }).getReferencesByElement(element)
        }
        return arrayOf()
    }
}

class EntityUsageConverter : BaseConvert<XmlAttributeValue>(), CustomReferenceConverter<XmlAttributeValue> {
    override fun fromString(s: String?, context: ConvertContext?): XmlAttributeValue? {
//        if (context != null && !s.isNullOrBlank() && context.xmlElement is XmlAttribute) {
//            var namespace = MapperUtil.getNamespace(context.file)
//            var entityName = s
//            if (s.contains(".")) {
//                namespace = s.substring(0, s.lastIndexOf("."))
//                entityName = s.substring(s.lastIndexOf(".") + 1)
//            }
//            return MapperUtil.getEntity(context.project, namespace, entityName, (context.xmlElement as XmlAttribute).name).firstOrNull()
//        }
        return null
    }

    override fun createReferences(value: GenericDomValue<XmlAttributeValue>?, element: PsiElement?, context: ConvertContext?): Array<PsiReference> {
        if (context != null && element != null && element is XmlAttributeValue) {
            var namespace = MapperUtil.getNamespace(context.file)
            var entityName = element.value
            if (entityName.contains(".")) {
                namespace = entityName.substring(0, entityName.lastIndexOf("."))
                entityName = entityName.substring(entityName.lastIndexOf(".")).replace(".", "")
            }
            return PsiClassConverter.createJavaClassReferenceProvider(value, null, object : JavaClassReferenceProvider() {

                override fun getReferencesByString(text: String?, position: PsiElement, offsetInPosition: Int): Array<PsiReference> {
                    val defaultReference = super.getReferencesByString(text, position, offsetInPosition).toMutableList()
                    MapperUtil.getEntity(context.project, namespace, entityName, (element.parent as XmlAttribute).name)
                        .map {
                            object : PsiReferenceBase<PsiElement>(element, getTextRange(position), false) {

                                override fun resolve(): PsiElement {
                                    return it
                                }

                                override fun getVariants(): Array<Any> {
                                    var str = getElement().text
                                    if (str.contains(".")) {
                                        str = str.substring(str.indexOf(".")).replace(".", "")
                                    }
                                    return arrayOf(str)
                                }
                            }
                        }.forEach {
                            defaultReference.add(it)
                        }
                    return defaultReference.toArray(arrayOf())
                }

                fun getTextRange(position: PsiElement): TextRange {
                    val t = position.text
                    return if (t.contains(".")) {
                        TextRange.create(t.substring(0, t.lastIndexOf(".")).length + 1, t.length - 1)
                    } else {
                        ElementManipulators.getValueTextRange(position)
                    }
                }
            }).getReferencesByElement(element)
        }
        return arrayOf()
    }
}

class EntityReferenceProvider(
    val value: String,
    val context: ConvertContext
) : JavaClassReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement): Array<PsiReference> {
        val defaultReference = super.getReferencesByElement(element).toMutableList()
        val customReference = MapperUtil.getEntityUsage(
            context.project,
            MapperUtil.getNamespace(context.file),
            value,
            (element.parent?.parent as XmlTag).name
        ).map {
            object : PsiReferenceBase<PsiElement>(it) {
                override fun resolve(): PsiElement {
                    return it
                }

                override fun getVariants(): Array<Any> {
                    var str = getElement().text
                    if (str.contains(".")) {
                        str = str.substring(str.indexOf(".")).replace(".", "")
                    }
                    return arrayOf(str)
                }
            }
        }.forEach {
            defaultReference.add(it)
        }
        return defaultReference.toArray(arrayOf())
    }
}
