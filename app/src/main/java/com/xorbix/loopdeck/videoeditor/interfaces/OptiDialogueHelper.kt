/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.xorbix.loopdeck.videoeditor.interfaces

import com.xorbix.loopdeck.videoeditor.fragment.OptiBaseCreatorDialogFragment
import java.io.File

interface OptiDialogueHelper {
    fun setHelper(helper: OptiBaseCreatorDialogFragment.CallBacks)
    fun setMode(mode: Int)
    fun setFilePathFromSource(file: File)
    fun duration(duration: Long)
}
