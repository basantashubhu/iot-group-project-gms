package com.example.gms

import android.app.Activity
import android.os.Bundle
import com.example.gms.databinding.ActivityAddBinBinding
import com.example.gms.firebase.FirebaseDB
import com.example.gms.models.Bin
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddBinActivity : BaseActivity() {
    private var _binding : ActivityAddBinBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.btnAddBin.setOnClickListener {
            if (!validates()) return@setOnClickListener
            addBin()
        }
    }

    private fun addBin() {
        showProgressDialog()
        FirebaseDB().addBin(this, Bin(
            name = sanitize(binding.etBinName),
            desc = sanitize(binding.etBinDesc)
        ))
    }

    fun onBinAddSuccess(task: Task<Void>) {
        hideProgressDialog()
        if (task.isSuccessful) {
            showSnackBar(SnackBarType.Success, "Bin added successfully.")
            CoroutineScope(Dispatchers.Main).launch {
                delay(1500)
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            showSnackBar(SnackBarType.Error, task.exception?.message.toString())
        }
    }

    private fun validates(): Boolean {
        var valid = true
        binding.tilBinName.error = null
        binding.tilBinDesc.error = null

        if (sanitize(binding.etBinName).isEmpty()) {
            valid = false
            binding.tilBinName.error = "Required"
        }
        if (sanitize(binding.etBinDesc).isEmpty()) {
            valid = false
            binding.tilBinDesc.error = "Required"
        }

        return valid
    }
}