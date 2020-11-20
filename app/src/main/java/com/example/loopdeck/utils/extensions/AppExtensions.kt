package com.example.loopdeck.utils.extensions

import com.example.loopdeck.data.MediaType
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import java.io.File

fun File.getMediaType(): String {
    return if (isImage()) MediaType.IMAGE else if (isVideo()) MediaType.VIDEO else MediaType.PLAYLIST
}