package fr.stein.maxbooker.ui.screens.settings.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel

class SettingsItemNotificationsViewModel : ViewModel() {
    fun isNotificationsEnabled(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()
}
