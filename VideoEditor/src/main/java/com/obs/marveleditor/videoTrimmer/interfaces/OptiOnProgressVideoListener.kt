/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */
package com.obs.marveleditor.videoTrimmer.interfaces

interface OptiOnProgressVideoListener {
    fun updateProgress(time: Int, max: Int, scale: Float)
}