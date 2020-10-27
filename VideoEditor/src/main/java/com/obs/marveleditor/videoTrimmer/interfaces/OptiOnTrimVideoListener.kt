/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */
package com.obs.marveleditor.videoTrimmer.interfaces

import android.net.Uri

interface OptiOnTrimVideoListener {
    fun onTrimStarted(startPosition: Int, endPosition: Int)
    fun getResult(uri: Uri?)
    fun cancelAction()
    fun onError(message: String?)
}