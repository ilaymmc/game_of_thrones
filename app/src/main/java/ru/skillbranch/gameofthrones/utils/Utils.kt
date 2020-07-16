package ru.skillbranch.gameofthrones.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.roundToInt


fun lastUrlSegment(s: String) = s.split("/").last()

val Context.isNetworkAvailable: Boolean
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

fun<T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()

    if (defaultValue != null) {
        data.value = defaultValue
    }

    return data
}

fun<T, A, B> LiveData<A>.combineAndCompute(other: LiveData<B>, onChange: (A, B) -> T): MutableLiveData<T> {
    var source1emitted = false
    var source2emitted = false

    val result = MediatorLiveData<T>()

    val mergeF = {
        val source1Value = this.value
        val source2Value = other.value
        if (source1emitted && source2emitted) {
            result.value = onChange.invoke(source1Value!!, source2Value!!)
        }
    }

    result.addSource(this) { source1emitted = true; mergeF.invoke() }
    result.addSource(other) { source2emitted = true; mergeF.invoke() }

    return result
}

fun Context.dpToPx(dp: Int) : Float {
    val displayMetrics = resources.displayMetrics
    return dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)
}