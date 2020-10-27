package com.xorbix.loopdeck.editor.filter

import com.xorbix.loopdeck.editor.photoeditor.PhotoFilter


interface FilterListener {
    fun onFilterSelected(photoFilter: PhotoFilter)
}