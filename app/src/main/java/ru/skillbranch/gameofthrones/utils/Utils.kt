package ru.skillbranch.gameofthrones.utils

import android.content.Context
import android.net.ConnectivityManager


fun lastUrlSegment(s: String) = s.split("/").last()

val Context.isNetworkAvailable: Boolean
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
