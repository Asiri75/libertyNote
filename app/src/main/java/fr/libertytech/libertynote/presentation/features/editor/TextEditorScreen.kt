package fr.libertytech.libertynote.presentation.features.editor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TextEditorScreen() {
    // State management for the text and mode
    var textState by remember { mutableStateOf(TextFieldValue("This is a sample text with a link https://www.example.com and some more text.")) }
    var isViewMode by remember { mutableStateOf(true) }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (isViewMode) {
            ViewingMode(
                text = textState.text,
                onLinkClick = { link ->
                    openLink(context, link)
                },
                onTextClick = { offset ->
                    textState = textState.copy(selection = TextRange(offset))
                    isViewMode = false
                }
            )
        } else {
            EditingMode(
                textFieldValue = textState,
                onLinkClick = { link ->
                    openLink(context, link)
                    isViewMode = true
                },
                onTextChange = {
                    textState = it
                }
            )
        }
    }
}

@Composable
private fun NoteTitle(
    noteTitle: String,
    onTextChange: (String) -> Unit,
) {
    BasicTextField(
        value = noteTitle,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            fontSize = MaterialTheme.typography.headlineMedium.fontSize, // Use a smaller size than headline
            fontWeight = FontWeight.Bold, // Make the text bold
            color = MaterialTheme.colorScheme.onBackground // Ensure the color adapts to the theme
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp) // Maintain padding from the original
    )
}

@Composable
fun ViewingMode(
    text: String,
    onLinkClick: (String) -> Unit,
    onTextClick: (Int) -> Unit
) {
    val annotatedText = createAnnotatedText(MaterialTheme.colorScheme.primary, text)

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let {
                    onLinkClick(it.item)
                } ?: onTextClick(offset)
        },
        style = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground // Text color adapts to theme
        ),
        modifier = Modifier.fillMaxSize()
    )
}


@Composable
fun EditingMode(
    textFieldValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onLinkClick: (String) -> Unit
) {
    // FocusRequester to request focus on the text field
    val focusRequester = remember { FocusRequester() }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val primaryColor = MaterialTheme.colorScheme.primary
    // Automatically request focus when entering the editing mode
    LaunchedEffect(Unit) {
        // Delay to ensure the UI is ready before requesting focus
        delay(100)
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newText ->
            // Capture the tap location from the new text layout
            textLayoutResult?.let { layoutResult ->
                val cursorPosition = newText.selection.start
                val annotatedText = createAnnotatedText(primaryColor, newText.text)

                annotatedText.getStringAnnotations(
                    tag = "URL",
                    start = cursorPosition,
                    end = cursorPosition
                ).firstOrNull()?.let {
                    // If a link is clicked, trigger onLinkClick
                    onLinkClick(it.item)
                } ?: run {
                    // If not a link, update the text field's value as usual
                    onTextChange(newText)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester), // Attach the FocusRequester
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground // Text color adapts to theme
        ),
        onTextLayout = { layoutResult ->
            textLayoutResult = layoutResult // Capture the layout result for offset calculations
        }, visualTransformation = {
            val annotatedText = createAnnotatedText(primaryColor, it.text)
            TransformedText(annotatedText, OffsetMapping.Identity)
        } // Show the annotated text with styled links

    )
}


fun createAnnotatedText(linkColor: Color, text: String): AnnotatedString {

    return buildAnnotatedString {
        // Regex to match URLs with or without "http"/"https"
        val urlPattern = "(https?://|http://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?".toRegex()
        val matches = urlPattern.findAll(text)

        var lastIndex = 0
        for (match in matches) {
            // Add the text before the link
            append(text.substring(lastIndex, match.range.first))
            // Add the link with an annotation and style using the primary color from the theme
            pushStringAnnotation(tag = "URL", annotation = match.value)
            withStyle(
                style = SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(match.value)
            }
            pop()
            lastIndex = match.range.last + 1
        }
        // Append any remaining text after the last link
        append(text.substring(lastIndex))
    }
}


fun openLink(context: Context, url: String) {
    // Normalize the URL
    val normalizedUrl = when {
        url.startsWith("http://") || url.startsWith("https://") -> url
        else -> "https://$url" // Default to https if no scheme is present
    }

    // Ensure the URL is valid before attempting to open it
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalizedUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        // Handle error: maybe show a toast or a dialog to the user indicating the URL is invalid
    }
}