package com.xorbix.loopdeck.gallery.presenter

import com.xorbix.loopdeck.gallery.model.interactor.PhotosInteractorImpl
import com.xorbix.loopdeck.gallery.view.PhotosFragment

class PhotosPresenterImpl(var photosFragment: PhotosFragment) : PhotosPresenter {
    val interactor: PhotosInteractorImpl = PhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}