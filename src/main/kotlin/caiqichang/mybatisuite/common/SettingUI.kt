package caiqichang.mybatisuite.common

import com.intellij.ui.TitledSeparator
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

object SettingUI : JPanel() {
    private fun readResolve(): Any = SettingUI

    val sqlLinePrefix = JTextField()
    val parameterLinePrefix = JTextField()
    val enableXmlMapperResolving = JCheckBox()
    
    fun setData(data: SettingService.Data) {
        sqlLinePrefix.text = data.sqlPrefix
        parameterLinePrefix.text = data.parameterPrefix
        enableXmlMapperResolving.isSelected = data.enableXmlMapperResolving
    }

    init {
        layout = GridBagLayout()
        var row = 0

        add(TitledSeparator("SQL Log"), pos(0, row++, true))
        val sqlLogPanel = JPanel(GridBagLayout()).apply {
            add(indent(), pos(0, 0))
            add(JLabel("SQL line prefix:"), pos(1, 0))
            add(sqlLinePrefix, pos(2, 0, true))
            add(JLabel("Parameter line prefix:"), pos(1, 1))
            add(parameterLinePrefix, pos(2, 1, true))
        }
        add(sqlLogPanel, pos(0, row++, true))

        add(TitledSeparator("Other"), pos(0, row++, true))
        val otherPanel = JPanel(GridBagLayout()).apply {
            add(indent(), pos(0, 0))
            add(enableXmlMapperResolving, pos(1, 0))
            add(JLabel("Enable XML mapper resolving (requires IDE restart)"), pos(2, 0, true))

        }
        add(otherPanel, pos(0, row++, true))
        
        // fill the rest space, make the vertical align to top
        add(JPanel(), pos(0, row).apply {
            fill = GridBagConstraints.VERTICAL
            weighty = 1.0
        })
    }

    private fun pos(x: Int, y: Int, fitWidth: Boolean = false) = GridBagConstraints().apply {
        gridx = x
        gridy = y
        anchor = GridBagConstraints.WEST
        if (fitWidth) {
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }
    }

    private fun indent() = JPanel().apply {
        preferredSize = Dimension(15, 1)
    }
}