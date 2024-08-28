package fr.libertytech.libertynote.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.libertytech.libertynote.R


@Composable
fun ContextualLinkActionsMenu(
    link: String,
    onEdit: () -> Unit,
    onCopy: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.elevatedCardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Link: $link")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onEdit) {
                    Text(stringResource(id = R.string.edit_link_text))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCopy) {
                    Text(stringResource(id = R.string.copy_link_text))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDismiss) {
                    Text(stringResource(id = R.string.close_button_text))
                }
            }
        }
    }
}