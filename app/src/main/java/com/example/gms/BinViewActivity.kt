package com.example.gms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gms.databinding.ActivityBinViewBinding
import com.example.gms.models.Bin

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
    }

    private fun handleBin() {
        binding.tvBinName.text = bin.name
        binding.tvBinDesc.text = bin.desc
        binding.tvBinState.text = "${bin.state?:0}%"
    }
}