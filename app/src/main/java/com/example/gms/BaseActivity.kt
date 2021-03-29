package com.example.gms

import android.app.Dialog
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    enum class SnackBarType {
        Success, Error, Warning, Info
    }

    private val mProgressDialog by lazy {
        Dialog(this).apply {
            setContentView(R.layout.dialog_progress)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun fullScreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    fun showProgressDialog(text : String = resources.getString(R.string.please_wait)) {
        mProgressDialog.findViewById<TextView>(R.id.progress_bar_text).text = text
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun showSnackBar(type : SnackBarType, message : String, snackbarLength : Int = Snackbar.LENGTH_LONG) {
        val color = when(type) {
            SnackBarType.Error -> R.color.danger
            SnackBarType.Success -> R.color.success
            SnackBarType.Warning -> R.color.warning
            SnackBarType.Info -> R.color.info
            else -> throw IllegalArgumentException("Could not find color name of [$type]")
        }
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, snackbarLength)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(this, color)
        )
        if (snackbarLength == Snackbar.LENGTH_INDEFINITE) {
            snackBar.setAction("Close") {
                snackBar.dismiss()
            }
        }
        snackBar.show()
    }

    open fun sanitize(input : EditText) : String{
        return input.text.toString().trim(' ')
    }
}