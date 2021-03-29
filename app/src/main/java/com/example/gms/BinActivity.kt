package com.example.gms

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gms.adapter.BinAdapter
import com.example.gms.databinding.ActivityBinBinding
import com.example.gms.firebase.FirebaseDB
import com.example.gms.models.Bin
import com.google.android.gms.tasks.Task

class BinActivity : BaseActivity() {
    private var _binding : ActivityBinBinding? = null
    private val binding get() = _binding!!

    private val addBinReqCode = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initList()
        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.fab.setOnClickListener {
            startActivityForResult(Intent(this, AddBinActivity::class.java), addBinReqCode)
        }

        binding.imgBackBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initList() {
        showProgressDialog("Loading bins...")
        binding.listItem.adapter = BinAdapter(this, emptyList())
        binding.listItem.layoutManager = LinearLayoutManager(this)

        FirebaseDB().getAllBins(this)
    }

    fun onFetchAllBins(binList: ArrayList<Bin>) {
        binding.listItem.adapter = BinAdapter(this, binList)
        hideProgressDialog()
    }

    fun deleteBin(bin: Bin) {
        val alert = AlertDialog.Builder(this).apply {
            setIcon(android.R.drawable.ic_menu_delete)
            setTitle("Delete Bin")
            setMessage("Are you sure you want to delete?")
            setCancelable(false)
            create()
        }
        alert.setPositiveButton("Delete") { _,_->
            attemptBinDelete(bin)
        }
        alert.setNegativeButton("Cancel") { _,_->

        }
        alert.show()
    }

    private fun attemptBinDelete(bin: Bin) {
        showProgressDialog()
        FirebaseDB().deleteBin(this, bin)
    }

    fun onBinDeleteComplete(task: Task<Void>) {
        hideProgressDialog()
        if (task.isSuccessful) {
            showSnackBar(SnackBarType.Success, "Bin deleted successfully.")
            initList()
        } else {
            showSnackBar(SnackBarType.Error, "An unexpected error occurred.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == addBinReqCode) {
            if (resultCode == Activity.RESULT_OK) {
                initList()
            }
        }
    }
}