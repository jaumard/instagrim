package com.jaumard.instagrim.ui.common

import android.app.Activity
import android.widget.Toast
import com.jaumard.instagrim.R
import com.jaumard.instagrim.network.exception.InstagramException
import javax.inject.Inject

open class CommonNavigator @Inject constructor(protected val context: Activity) {
    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showMessage(message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showGenericError() {
        showMessage(R.string.error_generic)
    }

    fun showError(error: Throwable) {
        if (error is InstagramException) {
            showMessage(error.message!!)
        } else {
            showGenericError()
        }
    }
}