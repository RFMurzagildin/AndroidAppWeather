package com.app.weatherapp

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogManager {
    fun locationSettingsDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Enable location?")
        dialog.setMessage("Location disabled, do you want enable location?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            listener.onClick()
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener {
        fun onClick()
    }
}