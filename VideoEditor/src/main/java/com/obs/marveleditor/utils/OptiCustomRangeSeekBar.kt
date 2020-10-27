/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */
//  The MIT License (MIT)
//  Copyright (c) 2018 Intuz Solutions Pvt Ltd.
//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
//  (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
//  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
//  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.obs.marveleditor.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.obs.marveleditor.R
import com.obs.marveleditor.interfaces.OptiOnRangeSeekBarChangeListener
import com.obs.marveleditor.utils.OptiBarThumb.Companion.getHeightBitmap
import com.obs.marveleditor.utils.OptiBarThumb.Companion.getWidthBitmap
import com.obs.marveleditor.utils.OptiBarThumb.Companion.initThumbs
import java.util.*

class OptiCustomRangeSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mHeightTimeLine = 0
    var thumbs: List<OptiBarThumb>? = null
        private set
    private var mListeners: MutableList<OptiOnRangeSeekBarChangeListener>? = null
    private var mMaxWidth = 0f
    private var mThumbWidth = 0f
    private var mThumbHeight = 0f
    private var mViewWidth = 0
    private var mPixelRangeMin = 0f
    private var mPixelRangeMax = 0f
    private var mScaleRangeMax = 0f
    private var mFirstRun = false
    private val mShadow = Paint()
    private val mLine = Paint()
    private fun init() {
        thumbs = initThumbs(resources)
        mThumbWidth = getWidthBitmap(thumbs!!).toFloat()
        mThumbHeight = getHeightBitmap(thumbs!!).toFloat()
        mScaleRangeMax = 100f
        mHeightTimeLine = context.resources.getDimensionPixelOffset(R.dimen._60sdp)
        isFocusable = true
        isFocusableInTouchMode = true
        mFirstRun = true
        val shadowColor = ContextCompat.getColor(context, R.color.colorAccent)
        mShadow.isAntiAlias = true
        mShadow.color = shadowColor
        mShadow.alpha = 177
        val lineColor = ContextCompat.getColor(context, R.color.colorAccent)
        mLine.isAntiAlias = true
        mLine.color = lineColor
        mLine.alpha = 200
    }

    fun initMaxWidth() {
        mMaxWidth = thumbs!![1].pos - thumbs!![0].pos
        onSeekStop(this, 0, thumbs!![0].`val`)
        onSeekStop(this, 1, thumbs!![1].`val`)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        mViewWidth = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val minH = paddingBottom + paddingTop + mThumbHeight.toInt()
        val viewHeight = resolveSizeAndState(minH, heightMeasureSpec, 1)
        setMeasuredDimension(mViewWidth, viewHeight)
        mPixelRangeMin = 0f
        mPixelRangeMax = mViewWidth - mThumbWidth
        if (mFirstRun) {
            for (i in thumbs!!.indices) {
                val th = thumbs!![i]
                th.`val` = mScaleRangeMax * i
                th.pos = mPixelRangeMax * i
            }
            // Fire listener callback
            onCreate(this, currentThumb, getThumbValue(currentThumb))
            mFirstRun = false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawShadow(canvas)
        drawThumbs(canvas)
    }

    private var currentThumb = 0
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val mBarThumb: OptiBarThumb
        val mBarThumb2: OptiBarThumb
        val coordinate = ev.x
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {

                // Remember where we started
                currentThumb = getClosestThumb(coordinate)
                if (currentThumb == -1) {
                    return false
                }
                mBarThumb = thumbs!![currentThumb]
                mBarThumb.lastTouchX = coordinate
                onSeekStart(this, currentThumb, mBarThumb.`val`)
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (currentThumb == -1) {
                    return false
                }
                mBarThumb = thumbs!![currentThumb]
                onSeekStop(this, currentThumb, mBarThumb.`val`)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mBarThumb = thumbs!![currentThumb]
                mBarThumb2 = thumbs!![if (currentThumb == 0) 1 else 0]
                // Calculate the distance moved
                val dx = coordinate - mBarThumb.lastTouchX
                val newX = mBarThumb.pos + dx
                if (currentThumb == 0) {
                    if (newX + mBarThumb.widthBitmap >= mBarThumb2.pos) {
                        mBarThumb.pos = mBarThumb2.pos - mBarThumb.widthBitmap
                    } else if (newX <= mPixelRangeMin) {
                        mBarThumb.pos = mPixelRangeMin
                        if (mBarThumb2.pos - (mBarThumb.pos + dx) > mMaxWidth) {
                            mBarThumb2.pos = mBarThumb.pos + dx + mMaxWidth
                            setThumbPos(1, mBarThumb2.pos)
                        }
                    } else {
                        //Check if thumb is not out of max width
//                        checkPositionThumb(mBarThumb, mBarThumb2, dx, true, coordinate);
                        if (mBarThumb2.pos - (mBarThumb.pos + dx) > mMaxWidth) {
                            mBarThumb2.pos = mBarThumb.pos + dx + mMaxWidth
                            setThumbPos(1, mBarThumb2.pos)
                        }
                        // Move the object
                        mBarThumb.pos = mBarThumb.pos + dx

                        // Remember this touch position for the next move event
                        mBarThumb.lastTouchX = coordinate
                    }
                } else {
                    if (newX <= mBarThumb2.pos + mBarThumb2.widthBitmap) {
                        mBarThumb.pos = mBarThumb2.pos + mBarThumb.widthBitmap
                    } else if (newX >= mPixelRangeMax) {
                        mBarThumb.pos = mPixelRangeMax
                        if (mBarThumb.pos + dx - mBarThumb2.pos > mMaxWidth) {
                            mBarThumb2.pos = mBarThumb.pos + dx - mMaxWidth
                            setThumbPos(0, mBarThumb2.pos)
                        }
                    } else {
                        //Check if thumb is not out of max width
//                        checkPositionThumb(mBarThumb2, mBarThumb, dx, false, coordinate);
                        if (mBarThumb.pos + dx - mBarThumb2.pos > mMaxWidth) {
                            mBarThumb2.pos = mBarThumb.pos + dx - mMaxWidth
                            setThumbPos(0, mBarThumb2.pos)
                        }
                        // Move the object
                        mBarThumb.pos = mBarThumb.pos + dx
                        // Remember this touch position for the next move event
                        mBarThumb.lastTouchX = coordinate
                    }
                }
                setThumbPos(currentThumb, mBarThumb.pos)

                // Invalidate to request a redraw
                invalidate()
                return true
            }
        }
        return false
    }

    private fun checkPositionThumb(
        mBarThumbLeft: OptiBarThumb,
        mBarThumbRight: OptiBarThumb,
        dx: Float,
        isLeftMove: Boolean,
        coordinate: Float
    ) {
        if (isLeftMove && dx < 0) {
            if (mBarThumbRight.pos - (mBarThumbLeft.pos + dx) > mMaxWidth) {
                mBarThumbRight.pos = mBarThumbLeft.pos + dx + mMaxWidth
                setThumbPos(1, mBarThumbRight.pos)
            }
        } else if (!isLeftMove && dx > 0) {
            if (mBarThumbRight.pos + dx - mBarThumbLeft.pos > mMaxWidth) {
                mBarThumbLeft.pos = mBarThumbRight.pos + dx - mMaxWidth
                setThumbPos(0, mBarThumbLeft.pos)
            }
        }
    }

    private fun pixelToScale(index: Int, pixelValue: Float): Float {
        val scale = pixelValue * 100 / mPixelRangeMax
        return if (index == 0) {
            val pxThumb = scale * mThumbWidth / 100
            scale + pxThumb * 100 / mPixelRangeMax
        } else {
            val pxThumb = (100 - scale) * mThumbWidth / 100
            scale - pxThumb * 100 / mPixelRangeMax
        }
    }

    private fun scaleToPixel(index: Int, scaleValue: Float): Float {
        val px = scaleValue * mPixelRangeMax / 100
        return if (index == 0) {
            val pxThumb = scaleValue * mThumbWidth / 100
            px - pxThumb
        } else {
            val pxThumb = (100 - scaleValue) * mThumbWidth / 100
            px + pxThumb
        }
    }

    private fun calculateThumbValue(index: Int) {
        if (index < thumbs!!.size && !thumbs!!.isEmpty()) {
            val th = thumbs!![index]
            th.`val` = pixelToScale(index, th.pos)
            onSeek(this, index, th.`val`)
        }
    }

    private fun calculateThumbPos(index: Int) {
        if (index < thumbs!!.size && !thumbs!!.isEmpty()) {
            val th = thumbs!![index]
            th.pos = scaleToPixel(index, th.`val`)
        }
    }

    private fun getThumbValue(index: Int): Float {
        return thumbs!![index].`val`
    }

    fun setThumbValue(index: Int, value: Float) {
        thumbs!![index].`val` = value
        calculateThumbPos(index)
        // Tell the view we want a complete redraw
        invalidate()
    }

    private fun setThumbPos(index: Int, pos: Float) {
        thumbs!![index].pos = pos
        calculateThumbValue(index)
        // Tell the view we want a complete redraw
        invalidate()
    }

    private fun getClosestThumb(coordinate: Float): Int {
        var closest = -1
        if (!thumbs!!.isEmpty()) {
            for (i in thumbs!!.indices) {
                // Find thumb closest to x coordinate
                val tcoordinate = thumbs!![i].pos + mThumbWidth
                if (coordinate >= thumbs!![i].pos && coordinate <= tcoordinate) {
                    closest = thumbs!![i].index
                }
            }
        }
        return closest
    }

    private fun drawShadow(canvas: Canvas) {
        if (!thumbs!!.isEmpty()) {
            for (th in thumbs!!) {
                if (th.index == 0) {
                    val x = th.pos
                    if (x > mPixelRangeMin) {
                        val mRect = Rect(
                            0,
                            (mThumbHeight - mHeightTimeLine).toInt() / 2,
                            (x + mThumbWidth / 2).toInt(),
                            mHeightTimeLine + (mThumbHeight - mHeightTimeLine).toInt() / 2
                        )
                        canvas.drawRect(mRect, mShadow)
                    }
                } else {
                    val x = th.pos
                    if (x < mPixelRangeMax) {
                        val mRect = Rect(
                            (x + mThumbWidth / 2).toInt(),
                            (mThumbHeight - mHeightTimeLine).toInt() / 2,
                            mViewWidth,
                            mHeightTimeLine + (mThumbHeight - mHeightTimeLine).toInt() / 2
                        )
                        canvas.drawRect(mRect, mShadow)
                    }
                }
            }
        }
    }

    private fun drawThumbs(canvas: Canvas) {
        if (!thumbs!!.isEmpty()) {
            for (th in thumbs!!) {
                if (th.index == 0) {
                    Objects.requireNonNull(th.bitmap)?.let {
                        canvas.drawBitmap(
                            it,
                            th.pos + paddingLeft,
                            paddingTop.toFloat(),
                            null
                        )
                    }
                } else {
                    Objects.requireNonNull(th.bitmap)?.let {
                        canvas.drawBitmap(
                            it,
                            th.pos - paddingRight,
                            paddingTop.toFloat(),
                            null
                        )
                    }
                }
            }
        }
    }

    fun addOnRangeSeekBarListener(listener: OptiOnRangeSeekBarChangeListener) {
        if (mListeners == null) {
            mListeners = ArrayList()
        }
        mListeners!!.add(listener)
    }

    private fun onCreate(CustomRangeSeekBar: OptiCustomRangeSeekBar, index: Int, value: Float) {
        if (mListeners == null) return
        for (item in mListeners!!) {
            item.onCreate(CustomRangeSeekBar, index, value)
        }
    }

    private fun onSeek(CustomRangeSeekBar: OptiCustomRangeSeekBar, index: Int, value: Float) {
        if (mListeners == null) return
        for (item in mListeners!!) {
            item.onSeek(CustomRangeSeekBar, index, value)
        }
    }

    private fun onSeekStart(CustomRangeSeekBar: OptiCustomRangeSeekBar, index: Int, value: Float) {
        if (mListeners == null) return
        for (item in mListeners!!) {
            item.onSeekStart(CustomRangeSeekBar, index, value)
        }
    }

    private fun onSeekStop(CustomRangeSeekBar: OptiCustomRangeSeekBar, index: Int, value: Float) {
        if (mListeners == null) return
        for (item in mListeners!!) {
            item.onSeekStop(CustomRangeSeekBar, index, value)
        }
    }

    init {
        init()
    }
}