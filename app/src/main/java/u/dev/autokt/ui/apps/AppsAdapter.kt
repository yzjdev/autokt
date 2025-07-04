package u.dev.autokt.ui.apps

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.android.appiconloader.AppIconLoader
import u.dev.autokt.bean.AppInfo
import u.dev.autokt.databinding.ItemAppBinding
import u.dev.autokt.kv
import java.util.concurrent.ConcurrentHashMap

class AppsAdapter(private val appIconLoader: AppIconLoader) :
    ListAdapter<AppInfo, AppsAdapter.ViewHolder>(AppDiffCallback()) {

    private val iconCache = ConcurrentHashMap<String, Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = getItem(position)


        holder.binding.switchWidget.isChecked = app.isChecked

        // 监听状态变化
        holder.binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
            app.isChecked = isChecked
        }

        val cachedIcon = iconCache[app.packageName]
        if (cachedIcon != null) {
            holder.binding.icon.setImageBitmap(cachedIcon)  // 使用缓存的图标
        } else {
            holder.binding.icon.setImageBitmap(null)

            // 使用协程加载图标
            CoroutineScope(Dispatchers.IO).launch {
                // 加载图标
                val icon = appIconLoader.loadIcon(app.info)

                // 保存到缓存
                iconCache[app.packageName] = icon

                // 更新UI
                withContext(Dispatchers.Main) {
                    holder.binding.icon.setImageBitmap(icon)
                }
            }
        }

        holder.binding.name.text = app.name
        holder.binding.packageName.text = app.packageName
    }


    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.switchWidget.setOnCheckedChangeListener(null)
    }

    inner class ViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)
}

class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
    override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
        return oldItem.packageName == newItem.packageName &&
                kv.getBoolean("{${oldItem.packageName}}_checked", false) == kv.getBoolean("{${newItem.packageName}}_checked", false)
    }
}
