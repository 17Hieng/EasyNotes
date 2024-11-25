package com.hieng.notes.presentation.screens.home.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.hieng.notes.BuildConfig
import com.hieng.notes.R
import com.hieng.notes.domain.model.Note
import com.hieng.notes.presentation.components.getNoteEnterAnimation
import com.hieng.notes.presentation.components.getNoteExitAnimation
import com.hieng.notes.presentation.screens.settings.model.SettingsViewModel

@Composable
fun NotesGrid(
    settingsViewModel: SettingsViewModel,
    containerColor: Color,
    onNoteClicked: (Int) -> Unit,
    shape: RoundedCornerShape,
    notes: List<Any>, // The list should now contain both Notes and NativeAds
    onNoteUpdate: (Note) -> Unit,
    selectedNotes: MutableList<Note>,
    viewMode: Boolean,
    isDeleteClicked: Boolean,
    animationFinished: (Int) -> Unit
) {
    val (pinnedItems, otherItems) = notes.partition { it is Note && it.pinned }

    @Composable
    fun NoteItem(note: Note, notes: List<Note>) {
        val isAnimationVisible = rememberTransitionState()
        AnimatedVisibility(
            visibleState = isAnimationVisible,
            enter = getNoteEnterAnimation(),
            exit = getNoteExitAnimation(calculateSlideDirection(notes, note))
        ) {
            NoteCard(
                settingsViewModel = settingsViewModel,
                containerColor = containerColor,
                note = note,
                shape = shape,
                isBorderEnabled = selectedNotes.contains(note),
                onShortClick = { handleShortClick(selectedNotes, note, onNoteClicked) },
                onNoteUpdate = onNoteUpdate,
                onLongClick = { handleLongClick(selectedNotes, note) }
            )
            if (isDeleteClicked && selectedNotes.contains(note)) {
                isAnimationVisible.targetState = false
            }
        }
        handleDeleteAnimation(selectedNotes, note, isAnimationVisible, animationFinished)
    }

    @Composable
    fun NativeAdItem(adUnit: String) {
        var isAdLoaded by remember { mutableStateOf(false) }
        var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
        val context = LocalContext.current

        LaunchedEffect(adUnit) {
            val adLoader = AdLoader.Builder(context, adUnit)
                .forNativeAd { ad ->
                    nativeAd = ad
                    isAdLoaded = true
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isAdLoaded = false
                    }
                })
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }

        if (isAdLoaded) {
            NativeAdCard() // Show the native ad card if it's loaded
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray)
            ) {
                Text(
                    text = "Loading ad...",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }
    }

    LazyVerticalStaggeredGrid(
        columns = if (viewMode) StaggeredGridCells.Fixed(2) else StaggeredGridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            if (pinnedItems.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = stringResource(id = R.string.pinned).uppercase(),
                        style = TextStyle(fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                    )
                }
                items(pinnedItems.filterIsInstance<Note>()) { note ->
                    NoteItem(note, pinnedItems.filterIsInstance<Note>())
                }
                if (otherItems.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp),
                            text = stringResource(id = R.string.others).uppercase(),
                            style = TextStyle(fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }

            items(otherItems) { item ->
                when (item) {
                    is Note -> NoteItem(note = item, notes = otherItems.filterIsInstance<Note>())
                    is NativeAd -> NativeAdItem(adUnit = BuildConfig.ADMOB_ADUNIT_ID)
                }
            }
        },
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

private fun calculateSlideDirection(notes: List<Note>, note: Note): Int {
    return if (notes.indexOf(note) % 2 == 0) -1 else 1
}

@Composable
private fun rememberTransitionState(): MutableTransitionState<Boolean> {
    return remember { MutableTransitionState(false).apply { targetState = true } }
}

private fun handleShortClick(
    selectedNotes: MutableList<Note>,
    note: Note,
    onNoteClicked: (Int) -> Unit
) {
    if (selectedNotes.isNotEmpty()) {
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note)
        } else {
            selectedNotes.add(note)
        }
    } else {
        onNoteClicked(note.id)
    }
}

private fun handleLongClick(selectedNotes: MutableList<Note>, note: Note) {
    if (!selectedNotes.contains(note)) {
        selectedNotes.add(note)
    }
}

private fun handleDeleteAnimation(
    selectedNotes: MutableList<Note>,
    note: Note,
    isAnimationVisible: MutableTransitionState<Boolean>,
    animationFinished: (Int) -> Unit
) {
    if (!isAnimationVisible.targetState && !isAnimationVisible.currentState && selectedNotes.contains(note)) {
        selectedNotes.remove(note)
        isAnimationVisible.targetState = true
        animationFinished(note.id)
    }
}