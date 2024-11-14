package caiqichang.mybatisuite.navigation

import caiqichang.mybatisuite.common.Icon
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement

open class BaseLineMarkerProvider : RelatedItemLineMarkerProvider() {

    fun getEnable() = true

    fun <T : PsiElement> addMarker(element: PsiElement, targets: Collection<T>, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (targets.isNotEmpty()) {
            result.add(
                NavigationGutterIconBuilder.create(Icon.LOGO)
                    .setTargets(targets)
                    .createLineMarkerInfo(element)
            )
        }
    }
}