package com.imagevideoeditor.soundpicker

class Songinfo {

    var Title: String? = null
    var Author: String? = null
    var SongUrl: String? = null
    var Duartion: String? = null
    var isPlaying: Boolean = false

    constructor(Title: String?, Author: String?, SongUrl: String?, Duartion: String?) {
        this.Title = Title
        this.Author = Author
        this.SongUrl = SongUrl
        this.Duartion = Duartion
    }
}