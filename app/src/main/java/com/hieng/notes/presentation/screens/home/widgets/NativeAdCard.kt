package com.hieng.notes.presentation.screens.home.widgets

import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_LEFT
import com.hieng.notes.BuildConfig
import com.hieng.notes.databinding.NativeAdCardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun NativeAdCard() {
    val context = LocalContext.current
    val adUnit: String = BuildConfig.ADMOB_ADUNIT_ID
    var isAdRequested by remember { mutableStateOf(false) }
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var adVisibility by remember { mutableStateOf(false) }

    // Load ad using LaunchedEffect
    LaunchedEffect(adUnit, isAdRequested) {
        if (!isAdRequested) {
            isAdRequested = true
            kotlin.runCatching {
                AdLoader.Builder(context, adUnit)
                    .forNativeAd { ad ->
                        nativeAd = ad
                        adVisibility = true // Mark as visible once ad is loaded
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            adVisibility = false // Hide if ad fails to load
                        }
                    })
                    .build()
                    .loadAd(AdRequest.Builder().build())
            }
        }
    }

    // Use AnimatedVisibility to control ad visibility
    AnimatedVisibility(visible = adVisibility) {
        // Use AndroidViewBinding to show the native ad using your custom layout
        AndroidViewBinding(
            factory = NativeAdCardBinding::inflate,
            modifier = Modifier
                .navigationBarsPadding()
                .wrapContentHeight(unbounded = true)
        ) {
            val adView = nativeAdView.also { adView ->
                adView.bodyView = adBody
                adView.callToActionView = adCtaButton
                adView.headlineView = adHeadline
                adView.mediaView = adMedia
            }

            // Populate the native ad's fields
            nativeAd?.let { ad ->
                ad.body?.let { body -> adBody.text = body }
                ad.callToAction?.let { cta -> adCtaButton.text = cta }
                ad.headline?.let { headline -> adHeadline.text = headline }
                adView.setNativeAd(ad)
            }
        }
    }

    // Fallback: Show loading state or placeholder when ad is not loaded
    if (!adVisibility) {
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