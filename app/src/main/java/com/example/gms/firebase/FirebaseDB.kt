package com.example.gms.firebase

import android.net.Uri
import android.util.Log
import com.example.gms.AddBinActivity
import com.example.gms.BinActivity
import com.example.gms.ProfileActivity
import com.example.gms.models.Bin
import com.example.gms.models.Store
import com.example.gms.models.User
import com.example.gms.ui.login.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseDB {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, user: User) {
        mFireStore.collection(Store.USERS)
            .document(user.uuid)
            .set(user, SetOptions.merge())
            .addOnCompleteListener { task ->
                activity.onRegistrationSuccess(task)
            }
    }

    private fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun getCurrentUser(callback: (User?) -> Unit) {
        mFireStore.collection(Store.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                callback(user)
            }

    }

    fun updateUser(activity: ProfileActivity, userInfo: HashMap<String, Any>) {
        mFireStore.collection(Store.USERS)
            .document(getCurrentUserID())
            .update(userInfo)
            .addOnCompleteListener { task ->
                activity.onUpdateComplete(task)
            }
    }


    fun uploadImage(activity: ProfileActivity, imageFileUri: Uri) {
        val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
            "user_profile_image${System.currentTimeMillis()}.${Store.getFileExt(activity, imageFileUri)}"
        )
        sRef.putFile(imageFileUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    activity.imageUploadSuccess(uri)
                }
            }
    }

    fun addBin(activity: AddBinActivity, bin : Bin) {
        mFireStore.collection(Store.BINS)
            .document()
            .set(bin, SetOptions.merge())
            .addOnCompleteListener { task ->
                activity.onBinAddSuccess(task)
            }
    }

    fun getAllBins(activity: BinActivity) {
        mFireStore.collection(Store.BINS)
            .get()
            .addOnSuccessListener { document ->
                val binList : ArrayList<Bin> = ArrayList()
                for (i in document.documents) {
                    val bin = i.toObject(Bin::class.java)!!
                    bin.uuid = i.id
                    binList.add(bin)
                }
                activity.onFetchAllBins(binList)
            }
    }

    fun deleteBin(activity: BinActivity, bin: Bin) {
        mFireStore.collection(Store.BINS)
            .document(bin.uuid)
            .delete()
            .addOnCompleteListener { task ->
                activity.onBinDeleteComplete(task)
            }
    }
}