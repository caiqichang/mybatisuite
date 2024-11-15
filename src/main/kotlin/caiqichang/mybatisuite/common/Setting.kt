package caiqichang.mybatisuite.common

import com.intellij.openapi.options.SearchableConfigurable

class Setting : SearchableConfigurable {

    private val id = "MyBatiSuite"

    override fun createComponent() = SettingUI

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {

    }

    override fun getDisplayName() = id

    override fun getId() = id

}