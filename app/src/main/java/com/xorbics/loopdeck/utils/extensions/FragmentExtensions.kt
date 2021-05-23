package com.xorbics.loopdeck.utils.extensions

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlin.jvm.Throws


fun Fragment.getContentView(): ViewGroup {
    return activity?.findViewById(android.R.id.content) as ViewGroup
}

fun Fragment.hideSoftKeyboard() {
    if (activity?.currentFocus != null) {
        val inputMethodManager = activity?.getSystemService(
            Context
                .INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus!!.windowToken, 0)
    }
}

fun Fragment.setToolbarTitle(
    title: String
) {
    activity?.actionBar?.title = title
}

@Throws(Resources.NotFoundException::class)
fun Fragment.getUri(@AnyRes resId: Int): Uri {
    val res = context?.resources
    return Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res?.getResourcePackageName(resId)
                + '/'.toString() + res?.getResourceTypeName(resId)
                + '/'.toString() + res?.getResourceEntryName(resId)
    )
}

fun Fragment.getDrawable(@DrawableRes id: Int): Drawable? = activity?.run { this.getDrawable(id) }


fun Fragment.toast(message: String) {
    activity?.run {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

fun Fragment.snackAtTop(message: String) {
    activity?.run {
        snackAtTop(message)

    }
}

fun Fragment.snackAtTop(message: String, action: () -> Unit) {
    activity?.run {
        snackAtTop(message, action)
    }
}


fun Fragment.snackAtBottom(message: String) {
    activity?.run {
        snackAtBottom(message)
    }
}

fun Fragment.snackAtBottom(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    actionText: String = "Retry",
    action: () -> Unit
) {
    activity?.run {
        snackAtBottom(message, duration, actionText, action)
    }
}

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, provider).get(VM::class.java)


inline fun <reified VM : ViewModel> Fragment.activityViewModelProvider(
) =
    ViewModelProviders.of(requireActivity()).get(VM::class.java)

inline fun <reified VM : ViewModel> Fragment.activityViewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(requireActivity(), provider).get(VM::class.java)

inline fun <reified VM : ViewModel> Fragment.parentFragmentViewModel(
    provider: ViewModelProvider.Factory
) =
    parentFragment?.let { ViewModelProviders.of(it, provider).get(VM::class.java) }
