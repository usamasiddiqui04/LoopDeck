/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.xorbix.loopdeck.videoeditor

import android.content.Context
import android.util.Log
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.xorbix.loopdeck.videoeditor.interfaces.OptiFFMpegCallback
import com.xorbix.loopdeck.videoeditor.utils.OptiConstant
import com.xorbix.loopdeck.videoeditor.utils.OptiOutputType
import java.io.File
import java.io.IOException
import java.nio.file.Paths


class OptiVideoEditor private constructor(private val context: Context) {

    private var tagName: String = OptiVideoEditor::class.java.simpleName
    private var videoFile: File? = null
    private var imageFile: File? = null
    var pathsList = java.util.ArrayList<Paths>()
    var FRAME_RATE: Int = 25
    private var multipleVideoFiles = mutableListOf<File>()

    private var videoFileTwo: File? = null
    private var callback: OptiFFMpegCallback? = null
    private var outputFilePath = ""
    private var type: Int? = null
    private var position: String? = null
    var videoPathOne: String? = null
    var videoFileThree: File? = null

    //for adding text
    private var font: File? = null
    private var text: String? = null
    private var color: String? = null
    private var size: String? = null
    private var border: String? = null
    private var BORDER_FILLED = ": box=1: boxcolor=black@0.5:boxborderw=5"
    private var BORDER_EMPTY = ""

    //for clip art
    private var imagePath: String? = null

    //for play back speed
    private var havingAudio = true
    private var ffmpegFS: String? = null

    //for merge audio video
    private var startTime = "00:00:00"
    private var endTime = "00:00:00"
    private var audioFile: File? = null
    private var listOfVideos: ArrayList<String>? = null

    //for filter
    private var filterCommand: String? = null

    companion object {
        fun with(context: Context): OptiVideoEditor {
            return OptiVideoEditor(context)
        }

        //for adding text
        var POSITION_BOTTOM_RIGHT = "x=w-tw-10:y=h-th-10"
        var POSITION_TOP_RIGHT = "x=w-tw-10:y=10"
        var POSITION_TOP_LEFT = "x=10:y=10"
        var POSITION_BOTTOM_LEFT = "x=10:h-th-10"
        var POSITION_CENTER_BOTTOM = "x=(main_w/2-text_w/2):y=main_h-(text_h*2)"
        var POSITION_CENTER_ALLIGN = "x=(w-text_w)/2: y=(h-text_h)/3"

        //for adding clipart
        var BOTTOM_RIGHT = "overlay=W-w-5:H-h-5"
        var TOP_RIGHT = "overlay=W-w-5:5"
        var TOP_LEFT = "overlay=5:5"
        var BOTTOM_LEFT = "overlay=5:H-h-5"
        var CENTER_ALLIGN = "overlay=(W-w)/2:(H-h)/2"
    }

    fun setType(type: Int): OptiVideoEditor {
        this.type = type
        return this
    }

    fun setVideoFile(file: File): OptiVideoEditor {
        this.videoFile = file
        return this
    }

    fun setImageFile(file: File): OptiVideoEditor {
        this.imageFile = file
        return this
    }

    fun setMutlipleFiles(files: List<File>): OptiVideoEditor {
        this.multipleVideoFiles.clear()
        this.multipleVideoFiles.addAll(files)
        return this
    }

    fun setAudioFile(file: File): OptiVideoEditor {
        this.audioFile = file
        return this
    }

    fun setCallback(callback: OptiFFMpegCallback): OptiVideoEditor {
        this.callback = callback
        return this
    }

    fun setImagePath(imagePath: String): OptiVideoEditor {
        this.imagePath = imagePath
        return this
    }

    fun setOutputPath(outputPath: String): OptiVideoEditor {
        this.outputFilePath = outputPath
        return this
    }

    fun setFilePathThree(file: File): OptiVideoEditor {
        videoFileThree = file
        return this
    }

    fun setFont(font: File): OptiVideoEditor {
        this.font = font
        return this
    }

    fun setText(text: String): OptiVideoEditor {
        this.text = text
        return this
    }

    fun setPosition(position: String): OptiVideoEditor {
        this.position = position
        return this
    }

    fun setColor(color: String): OptiVideoEditor {
        this.color = color
        return this
    }

    fun setSize(size: String): OptiVideoEditor {
        this.size = size
        return this
    }

    fun addBorder(isBorder: Boolean): OptiVideoEditor {
        if (isBorder)
            this.border = BORDER_FILLED
        else
            this.border = BORDER_EMPTY
        return this
    }

    fun setIsHavingAudio(havingAudio: Boolean): OptiVideoEditor {
        this.havingAudio = havingAudio
        return this
    }

    fun setSpeedTempo(playbackSpeed: String, tempo: String): OptiVideoEditor {
        this.ffmpegFS =
            if (havingAudio) "[0:v]setpts=$playbackSpeed*PTS[v];[0:a]atempo=$tempo[a]" else "setpts=$playbackSpeed*PTS"
        Log.v(tagName, "ffmpegFS: $ffmpegFS")
        return this
    }

    fun listOfVideos(videosList: ArrayList<String>) {
        listOfVideos = videosList
    }

    fun setStartTime(startTime: String): OptiVideoEditor {
        this.startTime = startTime
        return this
    }

    fun setEndTime(endTime: String): OptiVideoEditor {
        this.endTime = endTime
        return this
    }

    fun setFilter(filter: String): OptiVideoEditor {
        this.filterCommand = filter
        return this
    }

    fun setPathList(pathsList: ArrayList<Paths>): OptiVideoEditor {
        this.pathsList = pathsList
        return this
    }


    fun main() {
        if (type == OptiConstant.AUDIO_TRIM) {
            if (audioFile == null || !audioFile!!.exists()) {
                callback!!.onFailure(IOException("File not exists"))
                return
            }
            if (!audioFile!!.canRead()) {
                callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
                return
            }
        } else if (type == OptiConstant.MERGE_VIDEO) {
            if (multipleVideoFiles.isEmpty()) {
                callback!!.onFailure(IOException("File not exists"))
                return
            }
            multipleVideoFiles.forEach {
                if (!it.canRead()) {
                    callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
                    return
                }
            }

        } else if (type == OptiConstant.VIDEO_AUDIO_MERGE) {

            if ((videoFile == null && imageFile == null) || (videoFile?.exists() != true && imageFile?.exists() != true)) {
                callback!!.onFailure(IOException("File not exists"))
                return
            }
            if (videoFile?.canRead() == false || imageFile?.canRead() == false) {
                callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
                return
            }
        }


        val outputFile = File(outputFilePath)
        Log.v(tagName, "outputFilePath: $outputFilePath")
        var cmd: Array<String>? = null


        fun mergeImageAndAudio(): Array<String> {
            val inputs: ArrayList<String> = ArrayList()
            inputs.apply {
                add("-y")
                add("-loop")
                add("1")
                add("-i")
                add(imageFile!!.path)
                add("-i")
                add(audioFile!!.path)
                add("-shortest")
                add("-c:a")
                add("copy")
                add("-preset")
                add("ultrafast")
                add(outputFile!!.path)
            }
            return inputs.toArray(arrayOfNulls<String>(inputs.size))
        }
        when (type) {
            OptiConstant.VIDEO_FLIRT -> {
                //Video filter - Need video file, filter command & output file
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoFile!!.path,
                    "-vf",
                    filterCommand!!,
                    outputFile.path
                )
            }

            OptiConstant.VIDEO_TEXT_OVERLAY -> {
                //Text overlay on video - Need video file, font file, text, text color, text size, border if needed, position to apply & output file
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoFile!!.path,
                    "-vf",
                    "drawtext=fontfile=" + font!!.path + ": text=" + text + ": fontcolor=" + color + ": fontsize=" + size + border + ": " + position,
                    "-c:v",
                    "libx264",
                    "-c:a",
                    "copy",
                    "-movflags",
                    "+faststart",
                    outputFile.path
                )
            }

            OptiConstant.VIDEO_CLIP_ART_OVERLAY -> {
                //Clipart overlay on video - Need video file, image path, position to apply & output file
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoFile!!.path,
                    "-i",
                    imagePath!!,
                    "-filter_complex",
                    position!!,
                    "-codec:a",
                    "copy",
                    outputFile.path
                )
            }

            OptiConstant.MERGE_IMAGES -> {

            }


            OptiConstant.MERGE_VIDEO -> {
                val cmdList = mutableListOf<String>()

                cmdList += "-y"
                multipleVideoFiles.forEach { file ->
                    cmdList += "-i"
                    cmdList += file.path
                }

                cmdList += "-filter_complex"
                var tempStr = ""
                multipleVideoFiles.forEachIndexed { index, _ ->
                    tempStr += "[$index:v]scale=480x720,setsar=1[v$index];"
                }

                multipleVideoFiles.forEachIndexed { index, _ ->
                    tempStr += "[v$index][$index:a]"
                }

                tempStr += "concat=n=${multipleVideoFiles.size}:v=1:a=1"

                cmdList += tempStr

                cmdList += "-ab"
                cmdList += "48000"
                cmdList += "-ac"
                cmdList += "2"
                cmdList += "-ar"
                cmdList += "22050"
                cmdList += "-s"
                cmdList += "480x720"
                cmdList += "-vcodec"
                cmdList += "libx264"
                cmdList += "-crf"
                cmdList += "27"
                cmdList += "-preset"
                cmdList += "ultrafast"
                cmdList += outputFilePath

                cmd = cmdList.toTypedArray()


            }

            OptiConstant.VIDEO_PLAYBACK_SPEED -> {
                //Video playback speed - Need video file, speed & tempo value according to playback and output file
                cmd = if (havingAudio) {
                    arrayOf(
                        "-y",
                        "-i",
                        videoFile!!.path,
                        "-filter_complex",
                        ffmpegFS!!,
                        "-map",
                        "[v]",
                        "-map",
                        "[a]",
                        outputFile.path
                    )
                } else {
                    arrayOf(
                        "-y",
                        "-i",
                        videoFile!!.path,
                        "-filter:v",
                        ffmpegFS!!,
                        outputFile.path
                    )
                }
            }

            OptiConstant.AUDIO_TRIM -> {
                //Audio trim - Need audio file, start time, end time & output file
                cmd = arrayOf(
                    "-y",
                    "-i",
                    audioFile!!.path,
                    "-ss",
                    startTime,
                    "-to",
                    endTime,
                    "-c",
                    "copy",
                    outputFile.path
                )
            }

            OptiConstant.VIDEO_AUDIO_MERGE -> {

                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoFile!!.path,
                    "-i",
                    audioFile!!.path,
                    "-c",
                    "copy",
                    "-map",
                    "0:v:0",
                    "-map",
                    "1:a:0",
                    outputFile.path
                )
            }


            OptiConstant.IMAGE_AUDIO_MERGE -> {
                cmd = mergeImageAndAudio()
            }

            OptiConstant.VIDEO_AUDIO_OVERRIDE -> {

                //Video audio merge - Need audio file, video file & output file

//                ffmpeg -i video.mp4 -i audio.wav -c:v copy -c:a aac output.mp4

//                ffmpeg -i video.mp4 -i audio.wav -c copy output.mkv


//                ffmpeg -i audio.mp3 -i video.mp4 -filter_complex \
//                "[0:a][1:a]amerge,pan=stereo|c0<c0+c2|c1<c1+c3[a]" \
//                -map 1:v -map "[a]" -c:v copy -c:a aac -shortest output.mp4

//
//                ffmpeg -i video.mp4 -i audio.mp3 -c:v copy \
//                -filter_complex "[0:a]aformat=fltp:44100:stereo,apad[0a];[1]aformat=fltp:44100:stereo,volume=1.5[1a];[0a][1a]amerge[a]" \
//                -map 0:v -map "[a]" -ac 2 output.mp4


                cmd = arrayOf(
                    "-i",
                    audioFile!!.path,
                    "-i",
                    videoFile!!.path,
                    "-filter_complex",
                    "[0:a][1:a]amerge,pan=stereo:c0<c0+c2:c1<c1+c3[out]",
                    "-map",
                    "1:v",
                    "-map",
                    "[out]",
                    "-c:v",
                    "copy",
                    "-c:a",
                    "aac",
                    "-shortest",
                    outputFile.path
                )


//                cmd = arrayOf(
//                    "-y",
//                    "-i",
//                    audioFile!!.path,
//                    "-i",
//                    videoFile!!.path,
//                    outputFile.path
//                )
            }

            OptiConstant.VIDEO_TRIM -> {
                //Video trim - Need video file, start time, end time & output file
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoFile!!.path,
                    "-ss",
                    startTime,
                    "-t",
                    endTime,
                    "-c",
                    "copy",
                    outputFile.path
                )
            }

            OptiConstant.VIDEO_TRANSITION -> {
                //Video transition - Need video file, transition command & output file
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoFile!!.absolutePath,
                    "-acodec",
                    "copy",
                    "-vf",
                    "fade=t=in:st=0:d=5",
                    outputFile.path
                )
            }

            OptiConstant.CONVERT_AVI_TO_MP4 -> {
                //Convert .avi to .mp4 - Need avi video file, command, mp4 output file
                cmd = arrayOf(
                    "-y",
                    "-i",
                    videoFile!!.path,
                    "-c:v",
                    "libx264",
                    "-crf",
                    "19",
                    "-preset",
                    "slow",
                    "-c:a",
                    "aac",
                    "-b:a",
                    "192k",
                    "-ac",
                    "2",
                    outputFile.path
                )
            }

            OptiConstant.CHANGE_VIDEO_SOUND_FREQUENCY -> {
                //Video filter - Need video file, filter command & output file

                cmd = arrayOf(
                    "-i",
                    videoFile!!.path,
                    "-filter_complex",
                    "afftfilt=real='hypot(re,im)*sin(0)':imag='hypot(re,im)*cos(0)':win_size=10:overlap=0.75",
                    outputFile.path
                )

//                cmd = arrayOf(
//                    "-y",
//                    "-i",
//                    videoFile!!.path ,
//                    "-c",
//                    "copy",
//                    "-an",
//                    outputFile.path
//                )
            }


        }

//        val executionId = com.arthenica.mobileffmpeg.FFmpeg.executeAsync(
//            "-i ${videoFile!!.path} -filter_complex afftfilt=real='hypot(re,im)*sin(0)':imag='hypot(re,im)*cos(0)':win_size=10:overlap=0.75 ${outputFile.path}",
//            object : ExecuteCallback {
//
//                override fun apply(executionId: Long, returnCode: Int) {
//
//                    if (returnCode == RETURN_CODE_SUCCESS) {
//                        Log.i(
//                            Config.TAG,
//                            "Async command execution completed successfully."
//                        )
//
//                    } else if (returnCode == RETURN_CODE_CANCEL) {
//                        Log.i(
//                            Config.TAG,
//                            "Async command execution cancelled by user."
//                        )
//                    } else {
//                        Log.i(
//                            Config.TAG,
//                            String.format(
//                                "Async command execution failed with returnCode=%d.",
//                                returnCode
//                            )
//                        )
//                    }
//                }
//            })


        try {
            FFmpeg.getInstance(context).execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {

                }

                override fun onProgress(message: String?) {
                    Log.v(tagName, "FFmpeg command in Progress")
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    callback!!.onSuccess(outputFile, OptiOutputType.TYPE_VIDEO)
                }

                override fun onFailure(message: String?) {
                    if (outputFile.exists()) {
                        outputFile.delete()
                    }
                    callback!!.onFailure(IOException(message))
                }

                override fun onFinish() {
                    callback!!.onFinish()
                }
            })
        } catch (e: Exception) {
            callback!!.onFailure(e)
        } catch (e2: FFmpegCommandAlreadyRunningException) {
            callback!!.onNotAvailable(e2)
        }
    }
}