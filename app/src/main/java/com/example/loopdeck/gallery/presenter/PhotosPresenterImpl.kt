package com.example.loopdeck.gallery.presenter

import com.example.loopdeck.gallery.model.interactor.PhotosInteractorImpl
import com.example.loopdeck.gallery.view.PhotosFragment

class PhotosPresenterImpl(var photosFragment: PhotosFragment) : PhotosPresenter {
    val interactor: PhotosInteractorImpl = PhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}