package com.hieng.notes.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.glance.appwidget.updateAll
import androidx.hilt.navigation.compose.hiltViewModel
import com.hieng.notes.R
import com.hieng.notes.data.repository.SettingsRepositoryImpl
import com.hieng.notes.domain.usecase.NoteUseCase
import com.hieng.notes.presentation.components.NotesScaffold
import com.hieng.notes.presentation.screens.home.getContainerColor
import com.hieng.notes.presentation.screens.home.sorter
import com.hieng.notes.presentation.screens.home.widgets.NoteFilter
import com.hieng.notes.presentation.screens.settings.TopBar
import com.hieng.notes.presentation.screens.settings.model.SettingsViewModel
import com.hieng.notes.presentation.screens.settings.settings.shapeManager
import com.hieng.notes.presentation.theme.LeafNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class NotesWidgetActivity : ComponentActivity() {
    @Inject
    lateinit var noteUseCase: NoteUseCase

    @Inject
    lateinit var settingsRepository: SettingsRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        val appWidgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID,) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        setContent {
            val settings = hiltViewModel<SettingsViewModel>()
            noteUseCase.observe()

            LeafNotesTheme(settingsModel = settings) {
                NotesScaffold(
                    topBar = {
                        TopBar(
                            title = stringResource(id = R.string.select_note),
                            onBackNavClicked = { finish() }
                        )
                    },
                    content = {
                        NoteFilter(
                            settingsViewModel = settings,
                            containerColor = getContainerColor(settings),
                            shape = shapeManager(radius = settings.settings.value.cornerRadius / 2, isBoth = true),
                            onNoteClicked = { id ->
                                runBlocking {
                                    settingsRepository.putInt("${NotesWidgetReceiver.WIDGET_PREFERENCE}${appWidgetId}", id)
                                    NotesWidget().updateAll(this@NotesWidgetActivity)
                                    val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                    setResult(RESULT_OK, resultValue)
                                    finish()
                                }
                            },
                            notes = noteUseCase.notes.sortedWith(sorter(settings.settings.value.sortDescending)),
                            nativeAd = null,
                            viewMode = false,
                        )
                    }
                )
            }
        }
    }
}