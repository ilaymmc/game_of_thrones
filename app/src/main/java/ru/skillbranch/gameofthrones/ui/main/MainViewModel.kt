package ru.skillbranch.gameofthrones.ui.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.utils.isNetworkAvailable


class MainViewModel(val app: Application) : AndroidViewModel(app) {
    private val repository = RootRepository
    fun syncDataIfNeeded() : LiveData<LoadResult<Boolean>> {
        val result : MutableLiveData<LoadResult<Boolean>> = MutableLiveData(LoadResult.Loading(false))
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.isNeedUpdate()) {
                if (!app.applicationContext.isNetworkAvailable) {
                    result.postValue(LoadResult.Error("Нет доступа в интернет"))
                    return@launch
                }
                repository.updateAll()
                result.postValue(LoadResult.Success(true))
            } else {
                delay(5000)
                result.postValue(LoadResult.Success(false))
            }
        }
        return result
    }
}

sealed class LoadResult<T> (
    val data: T?,
    val errorMessage: String? = null
) {
    class Success<T>(data: T) : LoadResult<T>(data)
    class Loading<T>(data: T? = null) : LoadResult<T>(data)
    class Error<T>(errorMessage: String?, data: T? = null) : LoadResult<T>(data, errorMessage)
}