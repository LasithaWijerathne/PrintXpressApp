package com.printxpress.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.ui.auth.LoginActivity
import com.printxpress.app.ui.main.MainActivity

/**
 * Entry point of the app (see AndroidManifest's LAUNCHER intent-filter).
 * Its only job is to check whether a Firebase Auth session already
 * exists and route straight to MainActivity if so, skipping Login -
 * this is why Splash and Login/Register are the only two screens in the
 * Task B use-case diagram available to a guest.
 */
class SplashActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.printxpress.app.R.layout.activity_splash)

        // A short, deliberate delay so the brand moment is visible even on
        // a fast connection - the auth check itself is effectively
        // instant, since FirebaseAuth caches the session locally.
        Handler(Looper.getMainLooper()).postDelayed({
            val destination = if (authRepository.isLoggedIn) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(destination)
            finish()
        }, 900)
    }
}
