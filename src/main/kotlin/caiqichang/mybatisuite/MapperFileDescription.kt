package caiqichang.mybatisuite

import com.intellij.openapi.module.Module
import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomFileDescription

class MapperFileDescription : DomFileDescription<Mapper>(Mapper::class.java, "mapper") {

    val enable = true

    override fun isMyFile(file: XmlFile, module: Module?): Boolean {
        return enable && super.isMyFile(file, module)
    }
}