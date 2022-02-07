package com.app.eAcademicAssistant.utills

import android.app.Activity
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.widget.TextView
import java.lang.Exception


object SnackBarUtils {
    fun showSnackBar(activity: Activity, message: String) {
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        val snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
        val snackBarView = snackBar.view
        val tv =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        try {
            tv.textAlignment = View.TEXT_ALIGNMENT_CENTER

        } catch (e: Exception) {
            Log.e("______",e.message.toString())
        }
        snackBar.show()
    }
}