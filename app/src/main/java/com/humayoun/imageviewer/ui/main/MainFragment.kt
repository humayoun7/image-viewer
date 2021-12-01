package com.humayoun.imageviewer.ui.main

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.humayoun.imageviewer.Injection
import com.humayoun.imageviewer.R
import com.humayoun.imageviewer.constants.Constants
import com.humayoun.imageviewer.databinding.MainFragmentBinding
import com.humayoun.imageviewer.model.ImageInfo

/**
 * Main Fragment to control the UI
 */
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(requireActivity(), Injection.provideViewModelFactory()).get(MainViewModel::class.java)

        addListeners()
        if(viewModel.imageInfoListResults.value.isNullOrEmpty()){
            viewModel.getImageList()
        }

        // used when restoring state from config changes
        selectAndShowImage(false, binding.imageView)
    }



    fun loadNewImageUsingGlide (url: String = Constants.PicsumService.FALLBACK_IMAGE_URL, imageView: ImageView = binding.imageView) {
        binding.progressBar.visibility = View.VISIBLE
        binding.txtImageLoadTime.text =  "-"

        Glide
            .with(this)
            .load(url)
            .centerCrop()
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireActivity(), R.string.error_image_load, Toast.LENGTH_LONG).show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    val end = System.currentTimeMillis()
                    binding.progressBar.visibility = View.GONE
                    binding.txtImageLoadTime.text =   (end-viewModel.startTime).toString() +"ms"
                    return false
                }

            })
            .into(imageView);
    }

    fun addListeners () {
        // image list results observer
        viewModel.imageInfoListResults.observe(requireActivity(),Observer{
            // for initial load only
            if(viewModel.page == 1 && viewModel.currentIndex == 0) {
                selectAndShowImage(false, binding.imageView)
                selectAndShowImage(false, binding.imageView2)

            }
        })

        // imageview click listener
        binding.imageView.setOnClickListener(View.OnClickListener {
            selectAndShowImage(true, binding.imageView)
        })

        binding.imageView2.setOnClickListener(View.OnClickListener {
            selectAndShowImage(true, binding.imageView2)
        })
    }

    fun selectAndShowImage(showNext: Boolean, imageView: ImageView) {
        viewModel.imageInfoListResults.value?.let { imageInfoList ->
            if(imageInfoList.isNotEmpty()) {
                if (showNext) {
                    viewModel.updateConfig()
                }

                val imageInfo = imageInfoList.get(viewModel.currentIndex)
                viewModel.startTime = System.currentTimeMillis()
                setData(imageInfo, imageView)
            }
        }
    }

    fun setData(imageInfo: ImageInfo?, imageView: ImageView) {
        imageInfo?.downloadUrl?.let { it1 -> loadNewImageUsingGlide(it1, imageView) }
        imageInfo?.id?.let { it1 -> binding.txtImageId.text = it1 }
        imageInfo?.width?.let { it1 -> binding.txtImageWidth.text = it1.toString() + "px" }
        imageInfo?.height?.let { it1 -> binding.txtImageHeight.text = it1.toString() + "px" }
        imageInfo?.author?.let { it1 -> binding.txtAuthor.text = it1 }
    }
}