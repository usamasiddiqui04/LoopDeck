/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */
package com.obs.marveleditor.videoTrimmer.interfaces

import com.obs.marveleditor.videoTrimmer.view.OptiRangeSeekBarView

interface OptiOnRangeSeekBarListener {
    fun onCreate(rangeSeekBarView: OptiRangeSeekBarView?, index: Int, value: Float)
    fun onSeek(rangeSeekBarView: OptiRangeSeekBarView?, index: Int, value: Float)
    fun onSeekStart(rangeSeekBarView: OptiRangeSeekBarView?, index: Int, value: Float)
    fun onSeekStop(rangeSeekBarView: OptiRangeSeekBarView?, index: Int, value: Float)
}