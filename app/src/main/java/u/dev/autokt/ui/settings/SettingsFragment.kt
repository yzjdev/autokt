package u.dev.autokt.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import u.dev.autokt.R

class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }


}