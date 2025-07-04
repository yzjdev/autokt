package u.dev.autokt.ui.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import me.zhanghai.android.appiconloader.AppIconLoader
import u.dev.autokt.R
import u.dev.autokt.databinding.FragmentAppsBinding

class AppsFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAppsBinding? = null
    private val binding get() = _binding!!

    private lateinit var appIconLoader: AppIconLoader
    private lateinit var adapter: AppsAdapter
    private lateinit var appsViewModel: AppsViewModel

    private var showSystemApps = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Inflate the layout for this fragment
        _binding = FragmentAppsBinding.inflate(inflater, container, false)

        // 获取 ViewModel 实例
        appsViewModel = ViewModelProvider(this)[AppsViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appIconLoader = AppIconLoader(
            resources.getDimensionPixelSize(R.dimen.app_icon_size),
            false,
            requireContext().applicationContext
        )
        adapter = AppsAdapter(appIconLoader)
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.rv.adapter = adapter

        // 观察 apps 数据
        appsViewModel.appsLiveData.observe(viewLifecycleOwner) { apps ->
            adapter.submitList(apps)
        }

        // 检查权限并加载应用
        checkPermissionAndLoadApps()

    }

    private fun checkPermissionAndLoadApps() {
        val isGranted =
            XXPermissions.isGrantedPermissions(requireContext(), Permission.GET_INSTALLED_APPS)
        if (isGranted) {
            appsViewModel.loadApps(showSystemApps)
        } else {
            XXPermissions.with(requireContext()).permission(Permission.GET_INSTALLED_APPS)
                .request { _, all ->
                    if (all) {
                        appsViewModel.loadApps(showSystemApps)
                    }
                }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.apps_menu, menu)

        // 获取 SearchView 并设置监听器
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                appsViewModel.searchApps(newText)  // 输入变化时触发搜索
                return true
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (searchView.isIconified) {
                        requireActivity().moveTaskToBack(true)
                    } else {
                        // 否则，收起 SearchView
                        searchView.isIconified = true
                    }

                }
            }
        )
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.show_system_apps -> {
                menuItem.isChecked = !menuItem.isChecked
                showSystemApps = menuItem.isChecked
                appsViewModel.loadApps(showSystemApps)
                return true
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
