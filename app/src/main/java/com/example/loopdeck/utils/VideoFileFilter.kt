package com.example.loopdeck.utils

import java.io.File
import java.io.FileFilter

class VideoFileFilter : FileFilter {
    private val okFileExtensions = arrayOf(
        "mp4"
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