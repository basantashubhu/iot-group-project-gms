package com.example.gms

import android.content.Intent
import android.os.Bundle
import com.example.gms.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        fullScreenMode()

        handleResponses()
    }

    private fun handleResponses() {
        hideSplashScreen()
    }

    private fun hideSplashScreen() {
        CoroutineScope(Dispatchers.Main).launch {
            if (FirebaseAuth.getInstance().currentUser == null) {
                delay(1500)
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            } else {
                delay(1000)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}