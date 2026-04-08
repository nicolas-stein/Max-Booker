package fr.stein.maxbooker.ui.screens.bookings.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fr.stein.maxbooker.R

@Composable
fun BookingsDetailsActionMenu(menuExpanded: Boolean, onDismissRequest: () -> Unit, onDeleteClick: () -> Unit) {
    DropdownMenu(expanded = menuExpanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.delete))
            },
            leadingIcon = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete") },
            onClick = onDeleteClick
        )
    }
}
