package fr.stein.maxbooker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Preview
import fr.stein.maxbooker.ui.components.navigation.MainNavigation
import fr.stein.maxbooker.ui.theme.MaxBookerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaxBookerTheme {
                val currentIntent = rememberUpdatedState(intent)
                MainNavigation(initialDeepLinkUri = currentIntent.value?.data)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MaxBookerTheme {
        MainNavigation()
    }
}