package com.xorbix.loopdeck.gallery.presenter

import com.xorbix.loopdeck.gallery.model.interactor.VideosInteractorImpl
import com.xorbix.loopdeck.gallery.view.VideosFragment

class VideosPresenterImpl(var videosFragment: VideosFragment) : VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}