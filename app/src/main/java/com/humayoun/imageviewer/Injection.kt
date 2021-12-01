package com.humayoun.imageviewer

import androidx.lifecycle.ViewModelProvider
import com.humayoun.imageviewer.api.PicsumService
import com.humayoun.imageviewer.repository.ImageRepository
import com.humayoun.imageviewer.ui.main.ViewModelFactory

/**
 * Class that handles object creation.
 * objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 **/
object Injection {
    private fun providePicsumRepository(): ImageRepository {
        return ImageRepository(PicsumService.create())
    }

    // provides viewModelProvider.factory, that is then used to get reference of ViewModel class
    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ViewModelFactory(providePicsumRepository())
    }
}