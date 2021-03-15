package com.imagevideoeditor.filter.interfaces

import android.view.View

interface AddFilterListener {
    fun onFilterItemClicked(v: View, position: Int)
    fun onFilterItemDismissClicked(v: View, position: Int)
}
