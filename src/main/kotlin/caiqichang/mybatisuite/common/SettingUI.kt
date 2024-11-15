package caiqichang.mybatisuite.common

import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

object SettingUI : JPanel() {
    private fun readResolve(): Any = SettingUI

    private val sqlLinePrefix = JTextField()
    private val parameterLinePrefix = JTextField()
    private val enableXmlMapperResolving = JCheckBox() 
    
    init {
        add(JLabel("SQL Line Prefix:"))
        add(sqlLinePrefix)
        add(JLabel("Parameter Line Prefix:"))
        add(parameterLinePrefix)
        add(JLabel("Enable XML mapper Resolving:"))
        add(enableXmlMapperResolving)
    }
}