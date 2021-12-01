package com.humayoun.imageviewer.api

import com.humayoun.imageviewer.constants.Constants
import com.humayoun.imageviewer.model.ImageInfo
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Using retrofit for exposing picsum image service to make api calls
 * */

interface PicsumService {

    @GET("list")
    suspend fun getImageList(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ):  List<ImageInfo>


    companion object {
        fun create(): PicsumService{
            val httpClient = OkHttpClient.Builder()
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.PicsumService.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(httpClient)
                .build()

            return retrofit.create(PicsumService::class.java)
        }
    }
}