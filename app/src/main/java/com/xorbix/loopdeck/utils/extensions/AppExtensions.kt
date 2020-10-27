package com.xorbix.loopdeck.utils.extensions

import com.xorbix.loopdeck.data.MediaType
import com.xorbix.loopdeck.utils.isImage
import com.xorbix.loopdeck.utils.isVideo
import java.io.File

fun File.getMediaType(): String {
    return if (isImage()) MediaType.IMAGE else if (isVideo()) MediaType.VIDEO else MediaType.PLAYLIST
}