package fr.libertytech.libertynote.presentation.features.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString


@Composable
fun TextEditor() {
    var isViewMode by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("Test with https://www.example.com, let's go.") }
    var showTooltip by remember { mutableStateOf(false) }
    var selectedLink by remember { mutableStateOf<String?>(null) }

    if (isViewMode) {
        ViewingMode(
            text = text,
            onLinkClick = { link ->
                selectedLink = link
                showTooltip = true
            },
            onTextClick = {
                isViewMode = false
            }
        )
    } else {
        EditingMode(
            text = text,
            onTextChange = { newText ->
                text = newText
            },
            onLinkClick = { link ->
                selectedLink = link
                showTooltip = true
            }
        )
    }
}


@Composable
fun ViewingMode(text: String, onLinkClick: (String) -> Unit, onTextClick: () -> Unit) {
    val annotatedText = createAnnotatedText(text)
    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let {
                    onLinkClick(it.item)
                } ?: onTextClick()
        }
    )
}

@Composable
fun EditingMode(text: String, onTextChange: (String) -> Unit, onLinkClick: (String) -> Unit) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
    )
}

fun createAnnotatedText(text: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val urlPattern = "(https?://[\\w-]+(\\.[\\w-]+)+(/\\S*)?)".toRegex()
    val matches = urlPattern.findAll(text)

    var lastIndex = 0
    for (match in matches) {
        builder.append(text.substring(lastIndex, match.range.first))
        builder.pushStringAnnotation(tag = "URL", annotation = match.value)
        builder.append(match.value)
        builder.pop()
        lastIndex = match.range.last + 1
    }
    builder.append(text.substring(lastIndex))
    return builder.toAnnotatedString()
}