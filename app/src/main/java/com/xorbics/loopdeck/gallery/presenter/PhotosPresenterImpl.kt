package com.xorbics.loopdeck.gallery.presenter

import com.xorbics.loopdeck.gallery.model.interactor.PhotosInteractorImpl
import com.xorbics.loopdeck.gallery.view.PhotosFragment

class PhotosPresenterImpl(var photosFragment: PhotosFragment) : PhotosPresenter {
    val interactor: PhotosInteractorImpl = PhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}