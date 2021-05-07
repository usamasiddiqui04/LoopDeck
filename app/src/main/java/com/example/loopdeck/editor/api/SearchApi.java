package com.example.loopdeck.editor.api;


import com.example.loopdeck.editor.entities.SearchEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApi {

    @GET("?key=21361571-225988f8d640b5cab646fcbb6&per_page=200")
    Call<SearchEntity> getSearchResult(@Query("q") String query,
                                       @Query("image_type") String type,
                                       @Query("orientation") String orientation);
}