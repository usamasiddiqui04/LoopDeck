package com.example.loopdeck.gallery.model.interactor

import android.provider.MediaStore
import com.example.loopdeck.gallery.model.GalleryAlbums
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.gallery.presenter.PhotosPresenterImpl
import com.example.loopdeck.gallery.utils.MLog
import java.io.File

class PhotosInteractorImpl(var presenter: PhotosPresenterImpl) : PhotosInteractor {

    private fun getThumbnailPath(id: Long): String? {
        var result: String? = null
        val cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
            presenter.photosFragment.ctx.contentResolver,
            id,
            MediaStore.Images.Thumbnails.MINI_KIND,
            null
        )
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            result =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA))
            cursor.close()
        }
        return result
    }

    override fun getPhoneAlbums() {
        val galleryAlbums: ArrayList<GalleryAlbums> = ArrayList()
        val albumsNames: ArrayList<String> = ArrayList()

        val imagesProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.TITLE
        )
        val imagesQueryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imagescursor = presenter.photosFragment.ctx.contentResolver.query(
            imagesQueryUri,
            imagesProjection,
            null,
            null,
            null
        )

        MLog.e("IMAGES", imagescursor?.count.toString())

        try {
            if (imagescursor != null && imagescursor.count > 0) {
                if (imagescursor.moveToFirst()) {
                    val idColumn = imagescursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val dataColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val dateAddedColumn =
                        imagescursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                    val titleColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.TITLE)
                    do {
                        val id = imagescursor.getString(idColumn)
                        val data = imagescursor.getString(dataColumn)
                        val dateAdded = imagescursor.getString(dateAddedColumn)
                        val title = imagescursor.getString(titleColumn)
                        val file = File(data)
                        val galleryData = GalleryData(file = file)
                        galleryData.albumName = file.parentFile.name
                        galleryData.photoUri = file.absolutePath
                        galleryData.name = file.name
                        galleryData.file = file
                        galleryData.id = Integer.valueOf(id)
                        galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        galleryData.dateAdded = dateAdded
//                        galleryData.thumbnail = getThumbnailPath(galleryData.id.toLong()) ?: ""
//                        if (galleryData.thumbnail.isNotEmpty()) {
                        if (albumsNames.contains(galleryData.albumName)) {
                            for (album in galleryAlbums) {
                                if (album.name == galleryData.albumName) {
                                    galleryData.albumId = album.id
                                    album.albumPhotos.add(galleryData)
                                    presenter.photosFragment.photoList.add(galleryData)
                                    break
                                }
                            }
                        } else {
                            val album = GalleryAlbums()
                            album.id = galleryData.id
                            galleryData.albumId = galleryData.id
                            album.name = galleryData.albumName
                            album.coverUri = galleryData.photoUri
                            album.albumPhotos.add(galleryData)
                            presenter.photosFragment.photoList.add(galleryData)
                            galleryAlbums.add(album)
                            albumsNames.add(galleryData.albumName)
                        }
//                        }
                    } while (imagescursor.moveToNext())
                }
                imagescursor.close()
            } else presenter.photosFragment.listener.onError()
        } catch (e: Exception) {
            MLog.e("IMAGE PICKER", e.toString())
        } finally {
            presenter.photosFragment.listener.onComplete(galleryAlbums)
        }
    }

}