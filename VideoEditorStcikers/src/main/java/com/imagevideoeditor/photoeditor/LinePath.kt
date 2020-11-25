package com.imagevideoeditor.photoeditor

import android.graphics.Paint
import android.graphics.Path

class LinePath internal constructor(drawPath: Path?, drawPaints: Paint?) {
    val drawPaint: Paint
    val drawPath: Path

    init {
        drawPaint = Paint(drawPaints)
        this.drawPath = Path(drawPath)
    }
}