package com.example.loopdeck.editor

import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.R
import com.imagevideoeditor.Utils.DimensionData
import com.imagevideoeditor.Utils.Utils
import kotlinx.android.synthetic.main.activity_video_preview.*
import java.io.IOException

class VideoPreviewActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var videoPath: String? = null
    private var originalDisplayWidth = 0
    private var originalDisplayHeight = 0
    private var newCanvasWidth = 0
    private var newCanvasHeight = 0
    private var DRAW_CANVASW = 0
    private var DRAW_CANVASH = 0
    private val onCompletionListener =
        MediaPlayer.OnCompletionListener { mediaPlayer -> mediaPlayer.start() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_preview)
        videoPath = intent.getStringExtra("DATA")
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)
        val metaRotation =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
        val rotation = metaRotation?.toInt() ?: 0
        if (rotation == 90 || rotation == 270) {
            DRAW_CANVASH =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            DRAW_CANVASW =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        } else {
            DRAW_CANVASW =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            DRAW_CANVASH =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        }
        setCanvasAspectRatio()
        Log.d("new size>>", "W = $newCanvasWidth H = $newCanvasHeight")
        videoPreview!!.layoutParams.width = newCanvasWidth
        videoPreview!!.layoutParams.height = newCanvasHeight
        videoPreview!!.surfaceTextureListener =
            object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(
                    surfaceTexture: SurfaceTexture,
                    i: Int,
                    i1: Int
                ) {
//                activityHomeBinding.videoSurface.getLayoutParams().height=640;
//                activityHomeBinding.videoSurface.getLayoutParams().width=720;
                    val surface = Surface(surfaceTexture)
                    try {
                        mediaPlayer = MediaPlayer()
                        //                    mediaPlayer.setDataSource("http://daily3gp.com/vids/747.3gp");
                        Log.d("VideoPath>>", videoPath!!)
                        mediaPlayer!!.setDataSource(videoPath)
                        mediaPlayer!!.setSurface(surface)
                        mediaPlayer!!.prepare()
                        mediaPlayer!!.setOnCompletionListener(onCompletionListener)
                        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                        mediaPlayer!!.start()
                    } catch (e: IllegalArgumentException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    } catch (e: SecurityException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    } catch (e: IllegalStateException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    } catch (e: IOException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    }
                }

                override fun onSurfaceTextureSizeChanged(
                    surfaceTexture: SurfaceTexture,
                    i: Int,
                    i1: Int
                ) {
                }

                override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                    return false
                }

                override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
            }
    }

    private fun setCanvasAspectRatio() {
        originalDisplayHeight = displayHeight
        originalDisplayWidth = displayWidth
        val displayDiamenion = Utils.getScaledDimension(
            DimensionData(
                DRAW_CANVASW, DRAW_CANVASH
            ),
            DimensionData(originalDisplayWidth, originalDisplayHeight)
        )
        newCanvasWidth = displayDiamenion.width
        newCanvasHeight = displayDiamenion.height
    }

    private val displayWidth: Int
        get() {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    private val displayHeight: Int
        get() {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
        }
    }
}