package fr.stein.maxbooker.ui.common

import androidx.compose.ui.graphics.vector.ImageVector

data class TabBarItem(
    val navigationTitle: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)
