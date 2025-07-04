package u.dev.autokt.ui.apps

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import u.dev.autokt.bean.AppInfo

class AppsViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData 用来保存所有应用数据
    private val _appsLiveData = MutableLiveData<List<AppInfo>>()
    val appsLiveData: LiveData<List<AppInfo>> = _appsLiveData

    // 当前的查询条件
    private var currentQuery: String? = null

    // 保存原始的应用列表
    private var allApps = listOf<AppInfo>()

    // 加载应用列表
    fun loadApps(showSystemApps: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val pm = context.packageManager

            // 获取所有已安装的应用
            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter {
                    if (showSystemApps) {
                        true
                    } else {
                        (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                    }
                }
                .map { appInfo ->
                    // 创建 AppInfo 对象，AppInfo 需要从 ApplicationInfo 转换
                    AppInfo.from(appInfo)
                } .sortedBy { it.name.lowercase()}

            // 保存所有应用数据
            allApps = apps

            // 根据当前的查询过滤应用列表
            filterApps()
        }
    }

    // 根据查询过滤应用
    fun searchApps(query: String?) {
        currentQuery = query
        filterApps()
    }

    // 执行过滤逻辑
    private fun filterApps() {
        // 在主线程更新 LiveData
        val query = currentQuery?.trim() ?: return _appsLiveData.postValue(allApps)

        // 使用 contains 来匹配应用名称和包名
        val filteredApps = allApps.filter { appInfo ->
            val appName = appInfo.name
            val appPackage = appInfo.info.packageName

            // 使用 contains 匹配应用名称和包名，忽略大小写
            appName.contains(query, ignoreCase = true) || appPackage.contains(query, ignoreCase = true)
        }

        // 将过滤后的应用列表更新到 LiveData
        _appsLiveData.postValue(filteredApps)
    }
}
