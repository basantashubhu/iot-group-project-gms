package com.example.gms.ui.login

import android.content.Intent
import android.os.Bundle
import com.example.gms.BaseActivity
import com.example.gms.MainActivity
import com.example.gms.databinding.ActivityRegisterBinding
import com.example.gms.firebase.FirebaseDB
import com.example.gms.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterActivity : BaseActivity() {
    private var _binding : ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fullScreenMode()

        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.imgBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            if (!validates()) return@setOnClickListener
            attemptRegister(sanitize(binding.etEmail), sanitize(binding.etPassword))
        }
    }

    private fun attemptRegister(email: String, password: String) {
        showProgressDialog()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result!!.user!!
                    val user = User(
                        firebaseUser.uid,
                        sanitize(binding.etFirstName),
                        sanitize(binding.etLastName),
                        sanitize(binding.etEmail),
                    )
                    FirebaseDB().registerUser(this, user)
                } else {
                    hideProgressDialog()
                    showSnackBar(SnackBarType.Error, task.exception!!.message.toString(), 5000)
                }
            }
    }

    fun onRegistrationSuccess(task: Task<Void>) {
        hideProgressDialog()
        if (task.isSuccessful) {
            showSnackBar(SnackBarType.Success, "You are registered successfully.")
            CoroutineScope(Dispatchers.Main).launch {
                delay(1500)
                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                finish()
            }
        } else {
            showSnackBar(SnackBarType.Error, task.exception!!.message.toString(), 5000)
        }
    }

    private fun validates(): Boolean {
        var valid = true
        binding.tilFirstName.error = null
        binding.tilLastName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        if (sanitize(binding.etFirstName).isEmpty()) {
            valid = false
            binding.tilFirstName.error = "Required"
        }
        if (sanitize(binding.etLastName).isEmpty()) {
            valid = false
            binding.tilLastName.error = "Required"
        }
        if (sanitize(binding.etEmail).isEmpty()) {
            valid = false
            binding.tilEmail.error = "Required"
        }
        if (sanitize(binding.etPassword).isEmpty()) {
            valid = false
            binding.tilPassword.error = "Required"
        }
        if (sanitize(binding.etPassword) != sanitize(binding.etConfirmPassword)) {
            valid = false
            binding.tilConfirmPassword.error = "Password confirmation did not match password"
        }

        return valid
    }

}