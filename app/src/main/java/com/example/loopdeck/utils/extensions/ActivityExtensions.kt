package com.example.loopdeck.utils.extensions

import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar


fun FragmentActivity.getContentView(): ViewGroup {
    return this.findViewById(android.R.id.content) as ViewGroup
}

fun FragmentActivity.hideSoftKeyboard() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(
            Context
                .INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}


fun FragmentActivity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


fun FragmentActivity.snackAtTop(message: String) {
    val snack = Snackbar.make(getContentView(), message, Snackbar.LENGTH_LONG)
    val view = snack.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params
    snack.show()
}

fun FragmentActivity.snackAtTop(message: String, action: () -> Unit) {
    val snack = Snackbar.make(getContentView(), message, Snackbar.LENGTH_LONG)
        .setAction("Retry") { action() }
    val view = snack.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params
    snack.show()
}

fun FragmentActivity.snackAtBottom(message: String) {
    val snackbar = Snackbar.make(getContentView(), message, Snackbar.LENGTH_LONG)
    snackbar.show()
}


fun FragmentActivity.snackAtBottom(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    actionText: String = "Retry",
    action: () -> Unit
) {
    Snackbar.make(getContentView(), message, duration)
        .setAction(actionText) { action() }
        .show()
}


inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, provider).get(VM::class.java)


fun FragmentActivity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getContentView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getContentView().height - visibleBounds.height()
    val marginOfError = 50.px
    return heightDiff > marginOfError
}

fun FragmentActivity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}

inline fun <reified VM : ViewModel> FragmentActivity.activityViewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, provider).get(VM::class.java)


inline fun <reified VM : ViewModel> FragmentActivity.activityViewModelProvider() =
    ViewModelProviders.of(this).get(VM::class.java)