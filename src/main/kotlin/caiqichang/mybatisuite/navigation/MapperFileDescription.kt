package caiqichang.mybatisuite.navigation

import com.intellij.openapi.module.Module
import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomFileDescription

class MapperFileDescription : DomFileDescription<Mapper>(Mapper::class.java, "mapper") {

    val enable = true

    // must override, constructor and super function do not check the root tag name 
    override fun isMyFile(file: XmlFile, module: Module?) =
        file.rootTag != null && file.rootTag?.name == "mapper" && enable
}