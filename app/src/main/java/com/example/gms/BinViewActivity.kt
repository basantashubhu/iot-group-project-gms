package com.example.gms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gms.databinding.ActivityBinViewBinding
import com.example.gms.firebase.FirebaseDB
import com.example.gms.models.Bin
import com.example.gms.notification.DisplayNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BinViewActivity : AppCompatActivity() {
    private var _binding : ActivityBinViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var bin : Bin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBinViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.extras == null)
            finish()

        bin = intent.getSerializableExtra("bin") as Bin

        handleBin()

        handleResponses()
        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.btnLocation.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
        binding.imgBackBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun handleResponses() {
        CoroutineScope(Dispatchers.Main).launch {
            FirebaseDB().fetchBinState("device1") { state ->
                binding.tvBinState.text = "$state%"
                if (state.toDouble() >= 80) {
                    DisplayNotification(this@BinViewActivity).show(state)
                }
            }
            delay(5000)
            handleResponses()
        }
    }

    private fun handleBin() {
        binding.tvBinName.text = bin.name
        binding.tvBinDesc.text = bin.desc
        binding.tvBinState.text = "${bin.state?:0}%"
    }
}