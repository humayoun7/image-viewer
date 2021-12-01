package com.humayoun.imageviewer.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.humayoun.imageviewer.repository.ImageRepository

class MainViewModel(private val imageRepository: ImageRepository) : ViewModel() {

    val imageInfoListResults = imageRepository.imageInfoList
    var page = 1
    val limit = 100
    var currentIndex = 0
    var startTime = System.currentTimeMillis()



    fun getImageList () {
        Log.i("MainViewModel", "getImageList page: "+page+", currentIndex:"+currentIndex)
        imageRepository.getImageList(page, limit)
    }

    fun updateConfig () {
        Log.i("MainViewModel", "updateConfig page: "+page+", currentIndex:"+currentIndex)
        // reset page if all no more results are returned
        imageInfoListResults.value?.size?.let { size ->
            if(size ==0) {
                page = 0
                currentIndex = 0
                getImageList()
            }
        }

        // update currentIndex and page if at the end of the list
        if(currentIndex == limit) {
            page++
            currentIndex = 0
            getImageList()
        }
    }
}