package com.xorbics.loopdeck.utils.extensions

import com.xorbics.loopdeck.data.MediaType
import com.xorbics.loopdeck.utils.isImage
import com.xorbics.loopdeck.utils.isVideo
import java.io.File

fun File.getMediaType(): String {
    return if (isImage()) MediaType.IMAGE else if (isVideo()) MediaType.VIDEO else MediaType.PLAYLIST
}