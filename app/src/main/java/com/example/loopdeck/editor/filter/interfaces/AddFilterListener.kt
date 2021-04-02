package com.example.loopdeck.editor.filter.interfaces

import android.view.View

interface AddFilterListener {
    fun onFilterItemClicked(v: View, position: Int)
    fun onFilterItemDismissClicked(v: View, position: Int)
}
