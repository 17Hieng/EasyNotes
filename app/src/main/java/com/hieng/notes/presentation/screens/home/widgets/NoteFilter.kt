package com.hieng.notes.presentation.screens.home.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.hieng.notes.R
import com.hieng.notes.domain.model.Note
import com.hieng.notes.presentation.screens.settings.model.SettingsViewModel

@Composable
fun NoteFilter(
    settingsViewModel: SettingsViewModel,
    containerColor : Color,
    onNoteClicked: (Int) -> Unit,
    shape: RoundedCornerShape,
    notes: List<Note>,
    nativeAd: NativeAd?,
    searchText: String? = null,
    selectedNotes: MutableList<Note> = mutableListOf(),
    viewMode: Boolean = false,
    isDeleteMode: Boolean = false,
    onNoteUpdate: (Note) -> Unit = {},
    onDeleteNote: (Int) -> Unit = {}
) {
    val filteredNotes = filterNotes(notes, searchText)
    val items = filterNotesWithAds(filterNotes(notes, searchText), nativeAd)

    if (filteredNotes.isEmpty()) {
        Placeholder(
            placeholderIcon = {
                Icon(
                    getEmptyIcon(searchText),
                    contentDescription = "Placeholder icon",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(64.dp)
                )
            },
            placeholderText = getEmptyText(searchText)
        )
    } else {
        NotesGrid(
            settingsViewModel = settingsViewModel,
            containerColor = containerColor,
            onNoteClicked = onNoteClicked,
            notes = items,
            shape = shape,
            onNoteUpdate = onNoteUpdate,
            selectedNotes = selectedNotes,
            viewMode = viewMode,
            isDeleteClicked = isDeleteMode,
            animationFinished = onDeleteNote
        )
    }
}

private fun filterNotes(notes: List<Note>, searchText: String?): List<Note> {
    return searchText?.takeIf { it.isNotBlank() }?.let { query ->
        notes.filter { note ->
            note.name.contains(query, ignoreCase = true) || note.description.contains(query, ignoreCase = true)
        }
    } ?: notes
}

private fun filterNotesWithAds(
    notes: List<Note>,
    nativeAd: NativeAd?,
    adPosition: Int = 15
): List<Any> {
    val items = mutableListOf<Any>()

    notes.forEachIndexed { index, note ->
        items.add(note)
        if (notes.size < adPosition && index == 0 && nativeAd != null) {
            items.add(nativeAd)
        }
        if ((index + 1) % adPosition == 0 && nativeAd != null) {
            items.add(nativeAd)
        }
    }

    return items
}

@Composable
private fun getEmptyText(searchText: String?): String {
    return when {
        searchText.isNullOrEmpty() -> stringResource(R.string.no_created_notes)
        else -> stringResource(R.string.no_found_notes)
    }
}

@Composable
private fun getEmptyIcon(searchText: String?): ImageVector {
    return when {
        searchText.isNullOrEmpty() -> Icons.AutoMirrored.Rounded.Notes
        else -> Icons.Rounded.Search
    }
}
