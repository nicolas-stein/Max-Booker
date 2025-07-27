package fr.stein.maxbooker.ui.screens.settings.login.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fr.stein.maxbooker.R
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.SncfCustomerCard
import fr.stein.maxbooker.ui.theme.MaxBookerTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun SettingsItemLogin(
    modifier: Modifier = Modifier,
    viewModel: SettingsItemLoginViewModel = hiltViewModel<SettingsItemLoginViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsItemLoginContent(
        modifier,
        uiState.sncfCustomer,
        uiState.isSncfCustomerLoading,
        uiState.sncfCustomerLoadingError)
}

@Composable
fun SettingsItemLoginContent(
    modifier: Modifier = Modifier,
    sncfCustomer: SncfCustomer? = null,
    isSncfCustomerLoading: Boolean = false,
    sncfCustomerLoadingError: Throwable? = null
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(stringResource(R.string.screen_settings_item_login_headline)) },
        supportingContent = {
            if (isSncfCustomerLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Match the height of the CircularProgressIndicator to the Text font size
                    val textStyle = LocalTextStyle.current
                    val fontSizeInDp = with(LocalDensity.current) { textStyle.fontSize.toDp() }

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(fontSizeInDp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.screen_settings_item_login_retrieving_user),
                        style = textStyle
                    )
                }
            } else if (sncfCustomerLoadingError != null) {
                Text(stringResource(R.string.screen_settings_item_login_retrieving_user_failed))
            } else if (sncfCustomer == null) {
                Text(stringResource(R.string.screen_settings_item_login_not_logged_in))
            } else {
                Text(stringResource(R.string.screen_settings_item_login_logged_in_as, "${sncfCustomer.firstName} ${sncfCustomer.lastName}"))
            }
        },
        trailingContent = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.screen_settings_item_login_headline)
            )
        }
    )
}

@Preview
@Composable
private fun SettingsItemLoginPreviewNotLoggedIn() {
    MaxBookerTheme {
        SettingsItemLoginContent()
    }
}

@Preview
@Composable
private fun SettingsItemLoginPreviewConnecting() {
    MaxBookerTheme {
        SettingsItemLoginContent(
            isSncfCustomerLoading = true
        )
    }
}

@Preview
@Composable
private fun SettingsItemLoginPreviewLoggedIn() {
    MaxBookerTheme {
        SettingsItemLoginContent(
            sncfCustomer = SncfCustomer(
                iuc = UUID.randomUUID().toString(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                civility = "M.",
                lastName = "Dupont",
                firstName = "Jean",
                birthDate = LocalDate.now().minusYears(20),
                language = "FR",
                address = "5 PLACE JUSSIEU",
                zipCode = "75005",
                city = "PARIS",
                country = "FRANCE",
                email = "jean.dupont@gmail.com",
                mobilePhone = "0033612345678",
                nsdStatus = "subscribed",
                pictureCounter = 1,
                pictureStatus = "notvalidated",
                maxTravelsPerDay = 2,
                pictureUpdate = LocalDateTime.now().minusYears(2),
                cniUpdate = LocalDateTime.now().minusYears(2),
                cniType = "CNICT",
                cniValue = "Verified",
                cards = listOf(SncfCustomerCard(
                    cardNumber = "0123456789",
                    marketingCarrierRef = "ABCDEF",
                    productType = "TGV_MAX_JEUNE",
                    contractStatus = "VALIDE",
                    validityStartDate = LocalDate.now().minusYears(2),
                    validityEndDate = LocalDate.now().plusYears(3),
                    ticketlessIndicator = true
                ))
            )
        )
    }
}