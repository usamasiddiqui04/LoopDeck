package com.xorbix.loopdeck

import com.xorbix.loopdeck.data.MediaData

class DragData(item: MediaData, width: Int, height: Int) {
    val item: MediaData
    val width: Int
    val height: Int

    init {
        this.item = item
        this.width = width
        this.height = height
    }
}