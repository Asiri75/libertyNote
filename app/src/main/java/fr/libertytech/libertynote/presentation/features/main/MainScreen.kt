package fr.libertytech.libertynote.presentation.features.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.libertytech.libertynote.R
import fr.libertytech.libertynote.presentation.features.editor.TextEditorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Wrapping the entire screen in the app's theme colors
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // App bar with logo and app name
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.routine_logo), // Use the image resource
                            contentDescription = null, // No content description for decorative image
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(40.dp) // Adjust the height to suit the design
                                .align(androidx.compose.ui.Alignment.Center) // Center the image in the app bar
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            // Main content
            TextEditorScreen()
        }
    }
}
