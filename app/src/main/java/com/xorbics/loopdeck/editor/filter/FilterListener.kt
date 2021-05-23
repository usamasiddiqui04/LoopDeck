package com.xorbics.loopdeck.editor.filter

import com.xorbics.loopdeck.editor.photoeditor.PhotoFilter


interface FilterListener {
    fun onFilterSelected(photoFilter: PhotoFilter)
}