package ru.skillbranch.gameofthrones

import android.app.Application
import ru.skillbranch.gameofthrones.utils.DatabaseService

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