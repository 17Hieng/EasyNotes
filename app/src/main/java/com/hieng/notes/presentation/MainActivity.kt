package com.hieng.notes.presentation

import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.MobileAds
import com.hieng.notes.BuildConfig
import com.hieng.notes.data.repository.SettingsRepositoryImpl
import com.hieng.notes.presentation.navigation.AppNavHost
import com.hieng.notes.presentation.screens.settings.model.SettingsViewModel
import com.hieng.notes.presentation.theme.LeafNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var settingsRepositoryImpl: SettingsRepositoryImpl

    val admobAppId = BuildConfig.ADMOB_APP_ID
    val admobAdUnitId = BuildConfig.ADMOB_ADUNIT_ID


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>()
            val noteId = intent?.getIntExtra("noteId", -1) ?: -1

            if (settingsViewModel.settings.value.gallerySync) {
                contentResolver.registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    true,
                    settingsViewModel.galleryObserver
                )
            }

            LeafNotesTheme(settingsViewModel) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    AppNavHost(settingsViewModel, noteId = noteId)
                }
            }
        }
        MobileAds.initialize(this) {

        }
    }
}
