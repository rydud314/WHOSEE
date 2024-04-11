package com.example.ncwp

import android.content.Context
import android.content.Intent
import android.net.Uri

object WebLauncher {

    fun launchWebsite(context: Context) {
        val url = "https://www.naver.com"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}