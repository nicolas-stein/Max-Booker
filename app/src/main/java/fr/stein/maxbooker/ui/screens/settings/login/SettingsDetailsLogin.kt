package fr.stein.maxbooker.ui.screens.settings.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailsLogin(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: SettingsDetailsLoginViewModel = hiltViewModel<SettingsDetailsLoginViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { SettingsDetailsLoginTopAppBar(
            onBackClick = navigateBack,
            onClearCookieClick = viewModel.topAppBarClearCookiesHandler,
            onRestartClick = viewModel.topAppBarRestartHandler,
            modifier = modifier
        ) }
    ) { innerPadding ->
        AndroidView(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            factory = { context ->
                viewModel.buildWebView(context, navigateBack)
            }, update = { view ->
                viewModel.updateWebView(view)
            }
        )
    }

    if (uiState.showLoginAuthenticationDialog) {
        LoginAuthenticationDialog(
            modifier = modifier,
            dialogState = uiState.loginAuthenticationDialogState,
            error = uiState.loginAuthenticationDialogError
        )
    }

    BackHandler {
        viewModel.handleBackPressed()
    }
}