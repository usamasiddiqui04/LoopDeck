package com.example.loopdeck.gallery.presenter

import com.example.loopdeck.gallery.model.interactor.VideosInteractorImpl
import com.example.loopdeck.gallery.view.VideosFragment

class VideosPresenterImpl(var videosFragment: VideosFragment) : VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}