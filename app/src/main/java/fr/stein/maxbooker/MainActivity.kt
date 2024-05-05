package fr.stein.maxbooker

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.material.color.DynamicColors
import com.google.gson.JsonParser
import fr.stein.maxbooker.alarm.AlarmSchedulerReceiver
import fr.stein.maxbooker.api.worker.SncfApiReservationsWorker
import fr.stein.maxbooker.api.worker.SncfApiTravelConfirmWorker
import fr.stein.maxbooker.api.worker.SncfApiTravelDetailsWorker
import fr.stein.maxbooker.api.worker.SncfApiUserWorker
import fr.stein.maxbooker.database.MaxBookerDatabase
import fr.stein.maxbooker.datastore.MaxBookerDataStore
import fr.stein.maxbooker.datastore.MaxBookerDataStore.Companion.appSettings
import fr.stein.maxbooker.ui.common.TabBarItem
import fr.stein.maxbooker.ui.common.TabView
import fr.stein.maxbooker.ui.common.theme.MaxBookerTheme
import fr.stein.maxbooker.ui.tab.TabsViewModel
import fr.stein.maxbooker.ui.tab.book.BookTab
import fr.stein.maxbooker.ui.tab.myreservations.MyReservationsTab
import fr.stein.maxbooker.ui.tab.myreservations.MyReservationsViewModel
import fr.stein.maxbooker.ui.tab.settings.SettingsTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private var loginActivityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null
    private var shouldLaunchLoginActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {

        val myReservationsViewModel = MyReservationsViewModel()

        val tabsViewModel = TabsViewModel()

        val workManager = WorkManager.getInstance(applicationContext)
        val maxBookerDatabase = MaxBookerDatabase.getInstance(applicationContext)
        val appSettingsDatastore = MaxBookerDataStore(applicationContext).getAppSettingsDatastore()

        DynamicColors.applyToActivitiesIfAvailable(application);

        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            loginActivityLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    tabsViewModel.viewModelScope.launch(Dispatchers.IO) {
                        MaxBookerDataStore(this@MainActivity).getSncfApiSettingsDatastore().updateData { currentData ->
                            val builder = currentData.toBuilder()

                            val sncfAuthToken = activityResult.data!!.getStringExtra("SNCF_AUTH_TOKEN")
                            if (sncfAuthToken != null) { builder.setAuthToken(sncfAuthToken) }

                            val sncfRefreshToken = activityResult.data!!.getStringExtra("SNCF_REFRESH_TOKEN")
                            if (sncfRefreshToken != null) { builder.setRefreshToken(sncfRefreshToken) }

                            val sncfCookies = activityResult.data!!.getStringExtra("SNCF_COOKIES")
                            if (sncfCookies != null) { builder.setCookies(sncfCookies) }

                            builder.build()
                        }
                        workManager.enqueueUniqueWork(SncfApiUserWorker.WORK_NAME, ExistingWorkPolicy.KEEP, tabsViewModel.sncfApiUserWorkRequest)
                    }
                }
            }

            if(shouldLaunchLoginActivity) {
                shouldLaunchLoginActivity = false
                loginActivityLauncher!!.launch(Intent(this, LoginActivity::class.java))
            }

            val myReservationsTab = TabBarItem(navigationTitle = "myReservations",
                title = "Mes réservations",
                selectedIcon = ImageVector.vectorResource(R.drawable.ic_my_reservations_tab_selected),
                unselectedIcon = ImageVector.vectorResource(R.drawable.ic_my_reservations_tab_unselected),
                badgeAmount = null)

            val bookTab = TabBarItem(navigationTitle = "book",
                title = "Réserver",
                selectedIcon = ImageVector.vectorResource(R.drawable.ic_my_reservations_tab_selected),
                unselectedIcon = ImageVector.vectorResource(R.drawable.ic_my_reservations_tab_unselected),
                badgeAmount = null)

            val settingsTab = TabBarItem(navigationTitle = "settings",
                title = "Paramètres",
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                badgeAmount = null)

            val tabBarItems = listOf(myReservationsTab, bookTab, settingsTab)

            val navController = rememberNavController()

            MaxBookerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold(bottomBar = { TabView(tabBarItems, navController) }) {innerPadding ->
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding))
                        {
                            NavHost(navController = navController, startDestination = myReservationsTab.navigationTitle) {
                                composable(myReservationsTab.navigationTitle) {
                                    MyReservationsTab(tabsViewModel, myReservationsViewModel, appSettingsDatastore.data, workManager, maxBookerDatabase)
                                }
                                composable(bookTab.navigationTitle) {
                                    BookTab()
                                }
                                composable(settingsTab.navigationTitle) {
                                    SettingsTab(fragmentManager = supportFragmentManager,
                                        appSettingsDatastore = appSettingsDatastore)
                                }
                            }
                        }
                    }
                }
            }

            LaunchedEffect(true) {
                workManager.enqueueUniqueWork(SncfApiUserWorker.WORK_NAME, ExistingWorkPolicy.KEEP, tabsViewModel.sncfApiUserWorkRequest)

                workManager.getWorkInfoByIdLiveData(tabsViewModel.sncfApiUserWorkRequest.id).observeForever { workInfo ->
                    if (workInfo != null){
                        if (workInfo.state == WorkInfo.State.FAILED) {
                            if(workInfo.outputData.getBoolean("networkException", false)) {
                                Log.e("Max Book", "SncfApiUserWork FAILED because of a network exception : ${workInfo.outputData.getString("networkExceptionDetail")}")
                            }
                            else {
                                Log.d("Max Book", "SncfApiUserWork FAILED !")
                                workManager.enqueueUniqueWork(SncfApiUserWorker.WORK_NAME, ExistingWorkPolicy.KEEP, tabsViewModel.sncfApiAuthTokenWorkRequest)
                            }
                        }
                        else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            Log.d("Max Book", "SncfApiUserWorkRequest SUCCEEDED !")
                            workManager.enqueueUniqueWork(SncfApiReservationsWorker.WORK_NAME, ExistingWorkPolicy.KEEP, myReservationsViewModel.sncfApiReservationsWorkRequest)
                        }
                    }
                }

                workManager.getWorkInfoByIdLiveData(tabsViewModel.sncfApiAuthTokenWorkRequest.id).observeForever { workInfo ->
                    if (workInfo != null) {
                        if(workInfo.state == WorkInfo.State.FAILED) {
                            if(workInfo.outputData.getBoolean("networkException", false)) {
                                Log.d("Max Book", "SncfApiAuthTokenWork FAILED because of a network exception : ${workInfo.outputData.getString("networkExceptionDetail")}")
                            }
                            else {
                                Log.d("Max Book", "SncfApiAuthTokenWork FAILED !")
                                startLoginActivity()
                            }
                        }
                        else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            Log.d("Max Book", "SncfApiAuthTokenWork SUCCEEDED !")
                            workManager.enqueueUniqueWork(SncfApiUserWorker.WORK_NAME, ExistingWorkPolicy.KEEP, tabsViewModel.sncfApiUserWorkRequest)
                        }
                    }
                }

                workManager.getWorkInfoByIdLiveData(myReservationsViewModel.sncfApiReservationsWorkRequest.id).observeForever { workInfo ->
                    if (workInfo != null) {
                        myReservationsViewModel.setRefreshingReservations(!workInfo.state.isFinished)
                        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            val alarmScheduleIntent = Intent(applicationContext, AlarmSchedulerReceiver::class.java)
                            alarmScheduleIntent.action = "fr.stein.maxbooker.SCHEDULE_ALARMS"
                            sendBroadcast(alarmScheduleIntent)
                            val reservations = JsonParser.parseString(workInfo.outputData.getString("reservations")).asJsonArray
                            workManager.enqueueUniqueWork(SncfApiTravelDetailsWorker.WORK_NAME, ExistingWorkPolicy.KEEP, reservations.map { it.asJsonObject }
                                .map { reservation ->
                                    OneTimeWorkRequestBuilder<SncfApiTravelDetailsWorker>()
                                        .setInputData(Data.Builder()
                                            .putString("orderId", reservation.get("orderId").asString)
                                            .putString("origin", reservation.get("origin").asString)
                                            .putString("destination", reservation.get("destination").asString).build())
                                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).build()
                            })
                        }
                    }
                }
            }
        }
    }

    private fun startLoginActivity() {
        if(loginActivityLauncher == null) {
            shouldLaunchLoginActivity = true
        } else {
            loginActivityLauncher!!.launch(Intent(this, LoginActivity::class.java))
        }
    }
}