package caiqichang.mybatisuite.common

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "caiqichang.mybatisuite.common.SettingService",
    storages = [Storage("MyBatiSuite_SettingService.xml")]
)
class SettingService : PersistentStateComponent<SettingService.Data> {
    
    companion object {
        fun instance() = ApplicationManager.getApplication().getService(SettingService::class.java)!!
    }

    // default setting values
    class Data(
        var sqlPrefix: String = "==>  Preparing:",
        var parameterPrefix: String = "==> Parameters:",
        var enableXmlMapperResolving: Boolean = true,
    )

    private var state = Data()

    override fun getState() = state

    override fun loadState(state: Data) {
        this.state = state
    }
}