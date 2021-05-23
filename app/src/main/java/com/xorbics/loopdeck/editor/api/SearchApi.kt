package com.xorbics.loopdeck.editor.api

import com.xorbics.loopdeck.editor.entities.SearchEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("?key=21361571-225988f8d640b5cab646fcbb6&per_page=200")
    fun getSearchResult(
        @Query("q") query: String,
        @Query("image_type") type: String? = "vectors",
        @Query("orientation") orientation: String? = "all"
    ): Call<SearchEntity>
}