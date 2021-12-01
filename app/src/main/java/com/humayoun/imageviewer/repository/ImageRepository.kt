package com.humayoun.imageviewer.repository

import androidx.lifecycle.MutableLiveData
import com.humayoun.imageviewer.api.PicsumService
import com.humayoun.imageviewer.model.ImageInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Repository class to manage the data from web service or persistence layer
 * This class is responsible for providing the data
 * */

class ImageRepository(private val picsumService: PicsumService) {
    val imageInfoList = MutableLiveData<List<ImageInfo>>()

    fun getImageList(page: Int, limit: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageListResults = picsumService.getImageList(page,limit)
            imageInfoList.postValue(imageListResults.shuffled())
        }
    }

}