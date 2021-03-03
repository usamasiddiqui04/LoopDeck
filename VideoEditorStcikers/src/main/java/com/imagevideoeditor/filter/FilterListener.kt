package com.imagevideoeditor.filter

import com.imagevideoeditor.photoeditor.PhotoFilter


interface FilterListener {
    fun onFilterSelected(photoFilter: PhotoFilter?)
}