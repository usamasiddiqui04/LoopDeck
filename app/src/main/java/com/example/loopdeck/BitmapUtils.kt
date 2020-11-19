package com.xorbix.loopdeck.cameraapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

internal object BitmapUtils {
    private const val FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider"

    private const val ROOT_DIRECTORY_NAME = "Loopdeck Media Files"

    @Throws(IOException::class)
    fun createTempImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.externalCacheDir
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    /**
     * Deletes image file for a given path.
     *
     * @param context   The application context.
     * @param imagePath The path of the photo to be deleted.
     */
    fun deleteImageFile(context: Context, imagePath: String?): Boolean {

        // Get the file
        val imageFile = File(imagePath)

        // Delete the image
        val deleted = imageFile.delete()

        // If there is an error deleting the file, show a Toast
        if (!deleted) {

        }
        return deleted
    }

    /**
     * Helper method for adding the photo to the system photo gallery so it can be accessed
     * from other apps.
     *
     * @param imagePath The path of the saved image
     */
    private fun galleryAddPic(context: Context, imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    @JvmStatic
    fun SaveVideo(context: Context, selectedImageURI: Uri?): File? {
        var newfile: File? = null
        try {

            val videoAsset =
                context.contentResolver.openAssetFileDescriptor(selectedImageURI!!, "r")
            val `in` = videoAsset!!.createInputStream()

            val storageDir =
                File(context.getExternalFilesDir(null)!!.absolutePath, ROOT_DIRECTORY_NAME)
//            val filepath = context.getExternalFilesDir(null)!!.absoluteFile
//            val dir = File(filepath!!.absolutePath, ROOT_DIRECTORY_NAME)
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            newfile = File(storageDir, "save_" + System.currentTimeMillis() + ".mp4")
            if (newfile.exists()) newfile.delete()
            val out: OutputStream = FileOutputStream(newfile)

            // Copy the bits from instream to outstream
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            `in`.close()
            out.close()
            return newfile
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return newfile

    }

    fun createPlatlist(context: Context, playlistName: String): File {
        val storageDir = File(context.getExternalFilesDir(null)!!.absolutePath, ROOT_DIRECTORY_NAME)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }


        val playlistDir = File(storageDir.absolutePath, playlistName)

        if (!playlistDir.exists()) {
            playlistDir.mkdirs()
        }


        return playlistDir

    }

    @JvmStatic
    fun saveImage(context: Context, image: Bitmap, playlistName: String? = null): File? {


        val storageDir = File(context.getExternalFilesDir(null)!!.absolutePath, ROOT_DIRECTORY_NAME)

        Log.d("SAVE", "Storage Directory: $storageDir")

        var rootDirSuccess = true
        if (!storageDir.exists()) {
            rootDirSuccess = storageDir.mkdirs()
        }
        var playlistDirSuccess = true

        var destination = storageDir

        if (playlistName != null) {
            val playlistDir = File(storageDir.absolutePath, playlistName)

            Log.d("SAVE", "Playlis Directory: $playlistDir")
            if (!playlistDir.exists()) {
                playlistDirSuccess = playlistDir.mkdirs()
            }
            destination = playlistDir
        }

        Log.d("SAVE", "Destination Directory: $destination")
        // Save the new Bitmap
        if (rootDirSuccess && playlistDirSuccess) {
            val min = 10000
            val max = 99999
            val random = Random().nextInt(max - min + 1) + min
            // Create the new file in the external storage
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val imageFileName = "JPEG_$timeStamp$random.jpg"


            val imageFile = File(destination, imageFileName)



            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
                return imageFile

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * Helper method for sharing an image.
     *
     * @param context   The image context.
     * @param imagePath The path of the image to be shared.
     */
    fun shareImage(context: Context, imagePath: String?) {
        // Create the share intent and start the share activity
        val imageFile = File(imagePath)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        val photoURI = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, imageFile)
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
        context.startActivity(shareIntent)
    }
}