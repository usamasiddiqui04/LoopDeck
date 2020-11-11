package com.example.loopdeck.utils

import java.io.File
import java.io.FileFilter

class ImageFileFilter : FileFilter {
    private val okFileExtensions = arrayOf(
        "jpg",
        "png",
        "gif",
        "jpeg"
    )

    override fun accept(file: File): Boolean {
        for (extension in okFileExtensions) {
            if (file.name.toLowerCase().endsWith(extension)) {
                return true
            }
        }
        return false
    }
}