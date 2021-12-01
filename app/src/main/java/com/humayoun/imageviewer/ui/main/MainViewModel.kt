package com.humayoun.imageviewer.ui.main

import androidx.lifecycle.ViewModel
import com.humayoun.imageviewer.repository.ImageRepository


/**
 * To control application/business logic
 */

class MainViewModel(private val imageRepository: ImageRepository) : ViewModel() {

    val imageInfoListResults = imageRepository.imageInfoList
    var page = 1
    val limit = 100
    var currentIndex = 0
    var startTime = System.currentTimeMillis()

    fun getImageList () {
        imageRepository.getImageList(page, limit)
    }

    fun updateConfig () {
        ++currentIndex
        // reset page if no more results are returned
        imageInfoListResults.value?.size?.let { size ->
            if(size == 0) {
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