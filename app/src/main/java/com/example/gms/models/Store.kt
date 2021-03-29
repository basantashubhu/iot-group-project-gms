package com.example.gms.models

import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.gms.ProfileActivity

object Store {
    val USERS = "users"
    val BINS = "bins"

    fun getFileExt(activity: ProfileActivity, uri: Uri) : String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}