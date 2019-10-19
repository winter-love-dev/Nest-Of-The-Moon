package com.example.nest_of_the_moon

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.URISyntaxException

open class KakaoWebViewClient(private val activity: Activity): WebViewClient()
{
    private val TAG = "KakaoWebViewClient: "

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean
    {

        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:"))
        {
            var intent: Intent? = null

            try
            {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME) //IntentURI처리
                Log.e(TAG, "shouldOverrideUrlLoading: try url: $url")

                val uri = Uri.parse(intent!!.dataString)
                Log.e(TAG, "shouldOverrideUrlLoading: uri: $uri")

                activity.startActivity(Intent(Intent.ACTION_VIEW, uri))

                return true
            }
            catch (ex: URISyntaxException)
            {
                Log.e(TAG, "shouldOverrideUrlLoading: URISyntaxException ex: $ex")
                return false
            }
            catch (e: ActivityNotFoundException)
            {
                Log.e(TAG, "shouldOverrideUrlLoading: ActivityNotFoundException e: $e")
                if (intent == null) return false

                val packageName = intent.getPackage() //packageName should be com.kakao.talk
                Log.e(TAG, "shouldOverrideUrlLoading: packageName: " + packageName!!)
                if (packageName != null)
                {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                    Log.e(TAG, "shouldOverrideUrlLoading: market://details?id=$packageName")
                    return true
                }
                return false
            }

        }

        return false
    }
}