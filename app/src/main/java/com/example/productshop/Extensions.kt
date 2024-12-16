package com.example.productshop

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.StringRes

fun Activity.makeToast(@StringRes string: Int) {
    Toast.makeText(this, getString(string), Toast.LENGTH_SHORT).show()
}

inline fun <reified T: Parcelable> Intent.getParcelableCompat(key: String): T? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return getParcelableExtra(key, T::class.java)
    } else {
        return getParcelableExtra<T>(key)
    }
}