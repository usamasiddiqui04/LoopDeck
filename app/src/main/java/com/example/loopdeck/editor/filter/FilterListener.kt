package com.example.loopdeck.editor.filter

import com.example.loopdeck.editor.photoeditor.PhotoFilter


interface FilterListener {
    fun onFilterSelected(photoFilter: PhotoFilter)
}