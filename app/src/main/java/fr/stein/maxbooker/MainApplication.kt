package fr.stein.maxbooker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import fr.stein.maxbooker.di.AppModules
import fr.stein.maxbooker.domain.utils.NotificationUtils
import fr.stein.maxbooker.domain.utils.WorkerScheduler
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class MainApplication :
    Application(),
    Configuration.Provider {
    @Inject @AppModules.ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var workerScheduler: WorkerScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()

        NotificationUtils.createNotificationChannels(applicationContext)
        applicationScope.launch {
            workerScheduler.scheduleWorkers()
        }
    }
}
