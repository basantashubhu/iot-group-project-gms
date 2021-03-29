package com.example.gms.ui.login

import android.content.Intent
import android.os.Bundle
import com.example.gms.BaseActivity
import com.example.gms.MainActivity
import com.example.gms.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {
    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fullScreenMode()

        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.btnLogin.setOnClickListener {
            if (!validates()) return@setOnClickListener
            attemptLogin(sanitize(binding.etUsername), sanitize(binding.etPassword))
        }

        binding.tvForgetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin(email: String, password: String) {
        showProgressDialog("Signing in")
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    showSnackBar(SnackBarType.Success, "You are logged in successfully.")
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1500)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    showSnackBar(SnackBarType.Error, task.exception!!.message.toString(), 5000)
                }
            }
    }

    private fun validates(): Boolean {
        var valid = true
        binding.tilUsername.error = null
        binding.tilPassword.error = null

        if (sanitize(binding.etUsername).isEmpty()) {
            valid = false
            binding.tilUsername.error = "Required"
        }
        if (sanitize(binding.etPassword).isEmpty()) {
            valid = false
            binding.tilPassword.error = "Required"
        }

        return valid
    }
}
