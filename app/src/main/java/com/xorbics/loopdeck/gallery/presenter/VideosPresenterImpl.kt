package com.xorbics.loopdeck.gallery.presenter

import com.xorbics.loopdeck.gallery.model.interactor.VideosInteractorImpl
import com.xorbics.loopdeck.gallery.view.VideosFragment

class VideosPresenterImpl(var videosFragment: VideosFragment) : VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}