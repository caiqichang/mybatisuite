package caiqichang.mybatisuite.navigation

import com.intellij.psi.xml.XmlFile
import com.intellij.util.xml.DomFileDescription

class MapperFileDescription : DomFileDescription<Mapper>(Mapper::class.java, "mapper") {

    // deprecated
    // must override, constructor and super function do not check the root tag name 
//    override fun isMyFile(file: XmlFile, module: Module?) =
//        MapperUtil.enableXmlMapperResolving() && file.rootTag != null && file.rootTag?.name == "mapper"

    override fun getAllowedNamespaces(namespaceKey: String, file: XmlFile): List<String?> {
        if (MapperUtil.enableXmlMapperResolving()) {
            return mutableListOf("mapper")
        }
        return mutableListOf()
    }
}