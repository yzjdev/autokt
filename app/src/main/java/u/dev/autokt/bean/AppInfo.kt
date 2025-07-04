package u.dev.autokt.bean

import android.content.pm.ApplicationInfo
import u.dev.autokt.kv
import u.dev.autokt.loadName
data class AppInfo(val info: ApplicationInfo,
                   val name: String = info.loadName(),
                   val packageName: String = info.packageName) {

    // 使用私有属性存储状态
    private var _checked: Boolean = kv.getBoolean("{${info.packageName}}_checked", false)

    // 自定义 getter
    var isChecked: Boolean
        get() = _checked  // 返回私有变量 _checked
        set(value) {
            _checked = value
            kv.putBoolean("{${info.packageName}}_checked", value)  // 更新 kv 存储
        }

    // 可以提供一个静态方法来获取当前 AppInfo 对象的实例（方便调用）
    companion object {
        fun from(info: ApplicationInfo): AppInfo {
            return AppInfo(info)
        }
    }
}
