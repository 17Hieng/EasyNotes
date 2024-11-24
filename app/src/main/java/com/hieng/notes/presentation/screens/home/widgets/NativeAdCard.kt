package com.hieng.notes.presentation.screens.home.widgets

import android.view.View
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_LEFT
import com.hieng.notes.BuildConfig
import com.hieng.notes.databinding.NativeAdCardBinding

@Composable
fun NativeAdCard() {
    val context = LocalContext.current
    val adUnit: String = BuildConfig.ADMOB_ADUNIT_ID
    var isAdRequested = false

    AndroidViewBinding(
        factory = NativeAdCardBinding::inflate,
        modifier = Modifier
            .navigationBarsPadding()
            .wrapContentHeight(unbounded = true)
    ) {

        // Set the views from the native ad content
        val adView = nativeAdView.also { adView ->
            adView.bodyView = adBody
            adView.callToActionView = adCtaButton
            adView.headlineView = adHeadline
            adView.mediaView = adMedia
        }

        adView.visibility = View.GONE

        kotlin.runCatching {
            AdLoader.Builder(adView.context, adUnit)
                .forNativeAd { nativeAd ->
                    nativeAd.body?.let { body ->
                        adBody.text = body
                    }

                    nativeAd.callToAction?.let { cta ->
                        adCtaButton.text = cta
                    }

                    nativeAd.headline?.let { headline ->
                        adHeadline.text = headline
                    }

                    adView.setNativeAd(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()

                        adView.visibility = View.VISIBLE
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        adView.visibility = View.GONE
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder().setAdChoicesPlacement(ADCHOICES_TOP_LEFT).build()
                )
                .build()
        }.onSuccess {
            it.loadAd(AdRequest.Builder().build())
            isAdRequested = true
        }
    }
}