package ru.skillbranch.gameofthrones

import android.app.Application
import ru.skillbranch.gameofthrones.data.local.DatabaseService

class App : Application() {

    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        DatabaseService.closeDb()
    }
    companion object {
        lateinit var instance : App
    }
}