package com.example.gms

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gms.databinding.ActivityMainBinding
import com.example.gms.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var _biding : ActivityMainBinding? = null
    private val binding get() = _biding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _biding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleAuth()
        handleClickEvents()
    }

    private fun handleAuth() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        binding.tvWelcomeText.text = "Welcome,\n${user.email}"
    }

    private fun handleClickEvents() {
        binding.btnLogOut.setOnClickListener {
            logout()
        }
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.btnBrowseBin.setOnClickListener {
            startActivity(Intent(this, BinActivity::class.java))
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("Are you you want to log out?")
            setIcon(android.R.drawable.ic_dialog_alert)
            create()
        }
        builder.setPositiveButton("Logout") { _,_ ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        builder.setNegativeButton("Cancel") {_,_->

        }
        builder.show()
    }
}