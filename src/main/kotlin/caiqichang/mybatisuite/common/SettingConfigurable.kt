package caiqichang.mybatisuite.common

import com.intellij.openapi.options.Configurable


class SettingConfigurable : Configurable {
    override fun createComponent() = SettingUI

    override fun isModified(): Boolean {
        val state = SettingService.instance().state
        return SettingUI.sqlLinePrefix.text != state.sqlPrefix
                || SettingUI.parameterLinePrefix.text != state.parameterPrefix
                || SettingUI.enableXmlMapperResolving.isSelected != state.enableXmlMapperResolving
    }

    override fun apply() {
        // set to default value if sqlLinePrefix or parameterPrefix is blank

        val state = SettingService.instance().state
        if (SettingUI.sqlLinePrefix.text.isNotBlank()) {
            state.sqlPrefix = SettingUI.sqlLinePrefix.text
        } else {
            state.sqlPrefix = SettingService.Data().sqlPrefix
        }
        if (SettingUI.parameterLinePrefix.text.isNotBlank()) {
            state.parameterPrefix = SettingUI.parameterLinePrefix.text
        } else {
            state.parameterPrefix = SettingService.Data().parameterPrefix
        }
        state.enableXmlMapperResolving = SettingUI.enableXmlMapperResolving.isSelected
    }

    override fun getDisplayName() = "MyBatiSuite"

    // this method called every time setting UI shown
    override fun reset() {
        SettingUI.setData(SettingService.instance().state)
    }
}