package ru.skillbranch.gameofthrones

import android.app.Application
import ru.skillbranch.gameofthrones.utils.DatabaseService

class App : Application() {

    override fun onCreate() {
        instanse = this
        super.onCreate()
//        DatabaseService.initDb(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        DatabaseService.closeDb()
    }
    companion object {
        lateinit var instanse : App
    }
}