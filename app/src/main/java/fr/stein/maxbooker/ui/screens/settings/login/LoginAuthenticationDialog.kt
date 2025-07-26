package fr.stein.maxbooker.ui.screens.settings.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.stein.maxbooker.R
import fr.stein.maxbooker.ui.components.lottie.LottieAnimationComponent
import fr.stein.maxbooker.ui.theme.MaxBookerTheme

enum class LoginAuthenticationDialogState {
    IN_PROGRESS,
    SUCECSS,
    FAILED
}

@Composable
fun LoginAuthenticationDialog(
    dialogState: LoginAuthenticationDialogState,
    modifier: Modifier = Modifier,
    error: Throwable? = null
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_account_circle),
                contentDescription = "Account"
            )
        },
        title = { Text(stringResource(R.string.screen_settings_item_login_headline)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (dialogState) {
                    LoginAuthenticationDialogState.IN_PROGRESS -> {
                        LottieAnimationComponent(
                            animation = R.raw.lottie_login_loop,
                            modifier = modifier.size(64.dp),
                            loop = true
                        )
                        Spacer(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        )
                        Text(stringResource(R.string.screen_settings_login_dialog_in_progress))
                    }
                    LoginAuthenticationDialogState.SUCECSS -> {
                        LottieAnimationComponent(
                            animation = R.raw.lottie_login_success,
                            modifier = modifier.size(64.dp),
                            loop = false
                        )
                        Spacer(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        )
                        Text(stringResource(R.string.screen_settings_login_dialog_success))
                    }
                    LoginAuthenticationDialogState.FAILED -> {
                        LottieAnimationComponent(
                            animation = R.raw.lottie_login_failed,
                            modifier = modifier.size(64.dp),
                            loop = false
                        )
                        Spacer(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        )
                        Text(stringResource(R.string.screen_settings_login_dialog_failed))
                    }
                }
            }
        },
        onDismissRequest = {},
        confirmButton = {}
    )
}

@Preview
@Composable
private fun LoginAuthenticationDialogPreview_IN_PROGRESS() {
    MaxBookerTheme {
        LoginAuthenticationDialog(LoginAuthenticationDialogState.IN_PROGRESS)
    }
}

@Preview
@Composable
private fun LoginAuthenticationDialogPreview_SUCCESS() {
    MaxBookerTheme {
        LoginAuthenticationDialog(LoginAuthenticationDialogState.SUCECSS)
    }
}

@Preview
@Composable
private fun LoginAuthenticationDialogPreview_FAILED() {
    MaxBookerTheme {
        LoginAuthenticationDialog(LoginAuthenticationDialogState.FAILED)
    }
}
