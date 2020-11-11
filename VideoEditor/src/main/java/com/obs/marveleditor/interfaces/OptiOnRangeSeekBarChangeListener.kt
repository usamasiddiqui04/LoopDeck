/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */
package com.obs.marveleditor.interfaces

import com.obs.marveleditor.utils.OptiCustomRangeSeekBar

interface OptiOnRangeSeekBarChangeListener {
    fun onCreate(CustomRangeSeekBar: OptiCustomRangeSeekBar?, index: Int, value: Float)
    fun onSeek(CustomRangeSeekBar: OptiCustomRangeSeekBar?, index: Int, value: Float)
    fun onSeekStart(CustomRangeSeekBar: OptiCustomRangeSeekBar?, index: Int, value: Float)
    fun onSeekStop(CustomRangeSeekBar: OptiCustomRangeSeekBar?, index: Int, value: Float)
}