package ru.skillbranch.gameofthrones

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import ru.skillbranch.gameofthrones.ui.MainActivity

const val MIN_DISPLAY_INTERVAL = 5000L

class SplashScreen: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        DatabaseService.initDb(applicationContext)
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.splash_screen)
        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = MIN_DISPLAY_INTERVAL
        Handler().postDelayed(
            {
                routeToMainPage()
                finish()
            },
            splashScreenDuration
        )
    }


    private fun routeToMainPage() {
        startActivity(Intent(this, MainActivity::class.java))
    }

}