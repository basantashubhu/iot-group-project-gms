package com.example.gms.ui.login

import android.os.Bundle
import com.example.gms.BaseActivity
import com.example.gms.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : BaseActivity() {
    private var _binding : ActivityForgetPasswordBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.btnRequest.setOnClickListener {
            if (!validates()) return@setOnClickListener
            requestForgetPassword(sanitize(binding.etUsername))
        }

        binding.imgBackBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun requestForgetPassword(email: String) {
        showProgressDialog()
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    showSnackBar(SnackBarType.Success, "Email sent successfully to reset you password.")
                } else {
                    showSnackBar(SnackBarType.Error, task.exception!!.message.toString(), 5000)
                }
            }
    }

    private fun validates(): Boolean {
        var valid = true
        binding.tilUsername.error = null

        if (sanitize(binding.etUsername).isEmpty()) {
            valid = false
            binding.tilUsername.error = "Required"
        }

        return valid
    }
}