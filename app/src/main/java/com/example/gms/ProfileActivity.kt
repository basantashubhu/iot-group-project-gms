package com.example.gms

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.RadioButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.example.gms.databinding.ActivityProfileBinding
import com.example.gms.firebase.FirebaseDB
import com.example.gms.models.User
import com.google.android.gms.tasks.Task
import java.io.IOException
import kotlin.math.log

class ProfileActivity : BaseActivity() {
    private var _binding : ActivityProfileBinding? = null
    private val binding get() = _binding!!

    private var currentUser : User? = null

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    private val permissionCode = 2

    private val imageRequestCode = 1
    private var selectedImageFileUri : Uri? = null
    private var profileImageUri : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reqPermission()

        fetchCurrentUser()
        handleClickEvents()
    }

    private fun reqPermission() {
        if (hasGranted()) return
        ActivityCompat.requestPermissions(this, permissions, permissionCode)
    }

    private fun hasGranted(): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun handleClickEvents() {
        binding.imgBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnProfileUpdate.setOnClickListener {
            if (!validates() || currentUser == null) return@setOnClickListener
            showProgressDialog()
            if (selectedImageFileUri == null) {
                updateProfile()
            } else {
                uploadImageAndUpdateProfile()
            }
        }

        binding.btnBrowseImage.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, imageRequestCode)
        }
    }

    private fun updateProfile() {
        val userInfo = HashMap<String, Any>()
        userInfo["first_name"] = sanitize(binding.etFirstName)
        userInfo["last_name"] = sanitize(binding.etLastName)
        userInfo["gender"] = getGender()
        if (profileImageUri != null) {
            userInfo["image"] = profileImageUri.toString()
        }
        FirebaseDB().updateUser(this, userInfo)
    }

    fun uploadImageAndUpdateProfile() {
        FirebaseDB().uploadImage(this, selectedImageFileUri!!)
    }

    fun onUpdateComplete(task: Task<Void>) {
        hideProgressDialog()
        if (task.isSuccessful) {
            showSnackBar(SnackBarType.Success, "You details are updated.")
        } else {
            showSnackBar(SnackBarType.Error, task.exception!!.message.toString(), 5000)
        }
    }

    fun imageUploadSuccess(imageUri: Uri) {
        profileImageUri = imageUri
        updateProfile()
    }

    private fun getGender(): String {
        return binding.radioGender.findViewById<RadioButton>(binding.radioGender.checkedRadioButtonId).text.toString()
    }

    private fun validates(): Boolean {
        var valid = true
        binding.tilFirstName.error = null
        binding.tilLastName.error = null
        binding.tilEmail.error = null
        binding.tilGender.error = null

        if (sanitize(binding.etFirstName).isEmpty()) {
            valid = false
            binding.tilFirstName.error = "Required"
        }
        if (sanitize(binding.etLastName).isEmpty()) {
            valid = false
            binding.etLastName.error = "Required"
        }
        if (sanitize(binding.etEmail).isEmpty()) {
            valid = false
            binding.etEmail.error = "Required"
        }
        if (binding.radioGender.checkedRadioButtonId == -1) {
            valid = false
            binding.tilGender.error = "Required"
        }

        return valid
    }

    private fun fetchCurrentUser() {
        showProgressDialog()
        FirebaseDB().getCurrentUser { user ->
            currentUser = user
            if (user != null) {
                binding.etFirstName.setText(user.first_name)
                binding.etLastName.setText(user.last_name)
                binding.etEmail.setText(user.email)
                binding.radioGender.children.forEach {
                    if (it is RadioButton) {
                        it.isChecked = user.gender == it.text.toString()
                    }
                }
                if (user.image != "") {
                    Glide.with(this).load(user.image).into(binding.imgProfile)
                }
            } else {
                showSnackBar(SnackBarType.Error, "Failed to fetch user details.", 5000)
            }
            hideProgressDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        selectedImageFileUri = data.data!!
                        Glide.with(this).load(selectedImageFileUri!!)
                            .centerCrop().into(binding.imgProfile)
                    } catch (ex : IOException) {
                        showSnackBar(SnackBarType.Error, "Failed to select an image.")
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(SnackBarType.Success, "Storage permission granted!")
            } else {
                showSnackBar(SnackBarType.Error, "Permission Denied!")
            }
        }
    }
}