package fr.libertytech.libertynote.presentation.features.editor

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("text_editor_prefs")

class TextEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val NOTE_TITLE_KEY = stringPreferencesKey("note_title")
    private val NOTE_TEXT_KEY = stringPreferencesKey("note_text")
    private val NOTE_TITLE_DEFAULT_VALUE = "Note title"
    private val NOTE_TEXT_DEFAULT_VALUE =
        "This is a sample text with a link https://www.libertytech.fr and some more text."

    private val _noteTitleState = MutableStateFlow(NOTE_TITLE_DEFAULT_VALUE)
    val noteTitleState: StateFlow<String> = _noteTitleState

    private val _noteState = MutableStateFlow(NOTE_TEXT_DEFAULT_VALUE)
    val noteState: StateFlow<String> = _noteState

    fun loadNote(context: Context) {
        viewModelScope.launch {
            val prefs = context.dataStore.data.first()
            _noteTitleState.value = prefs[NOTE_TITLE_KEY] ?: NOTE_TITLE_DEFAULT_VALUE
            _noteState.value = prefs[NOTE_TEXT_KEY] ?: NOTE_TEXT_DEFAULT_VALUE
        }
    }

    fun saveNoteTitle(context: Context, title: String) {
        _noteTitleState.value = title
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[NOTE_TITLE_KEY] = title
            }
        }
    }

    fun saveNoteText(context: Context, text: String) {
        _noteState.value = text
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[NOTE_TEXT_KEY] = text
            }
        }
    }
}
