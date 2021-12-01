package com.humayoun.imageviewer.ui.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Visibility
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.humayoun.imageviewer.Injection
import com.humayoun.imageviewer.R
import com.humayoun.imageviewer.databinding.MainFragmentBinding
import com.humayoun.imageviewer.model.ImageInfo
import java.util.concurrent.Executors
import kotlin.random.Random


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
        viewModel.getImageList()
        addObservers()
    }

    fun loadNewImageUsingGlide (url: String = "https://picsum.photos/400/600") {

        binding.progressBar.visibility = View.VISIBLE
        binding.txtImageLoadTime.text =  "-"
        Glide
            .with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .skipMemoryCache(true)
            //.placeholder(R.mipmap.loading)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    var end = System.currentTimeMillis()
                    binding.progressBar.visibility = View.GONE
                    binding.txtImageLoadTime.text =   (end-viewModel.startTime).toString() +"ms"
                    return false
                }

            })
            .into(binding.imageView);
    }

    fun loadNewImage (url: String = "https://picsum.photos/400/600") {
        // Declaring executor to parse the URL
        val executor = Executors.newSingleThreadExecutor()

        // Once the executor parses the URL
        // and receives the image, handler will load it
        // in the ImageView
        val handler = Handler(Looper.getMainLooper())

        // Initializing the image
        var image: Bitmap? = null

        // Only for Background process (can take time depending on the Internet speed)
        executor.execute {

            // Image URL
//            val imageURL = "https://media.geeksforgeeks.org/wp-content/cdn-uploads/gfg_200x200-min.png"

            // Tries to get the image and post it in the ImageView
            // with the help of Handler
            try {
                val `in` = java.net.URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)

                // Only for making changes in UI
                handler.post {
                    binding.imageView.setImageBitmap(image)
                }
            }

            // If the URL doesnot point to
            // image or any other kind of failure
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addObservers() {
        viewModel.imageInfoListResults.observe(requireActivity(),Observer{
            for (item in it) {
                item?.id?.let { it1 -> Log.i("Image Id", it1) }
            }

            // for initial load only
            if(viewModel.page == 1 && viewModel.currentIndex == 0) {
                selectAndShowImage()
            }

        })

        binding.imageView.setOnClickListener(View.OnClickListener {
            selectAndShowImage()

            Log.i("MainFragment", "clicked")
        })

    }

    fun selectAndShowImage() {
//        val random = viewModel.imageInfoListResults.value?.let { it1 -> Random.nextInt(0, it1.size) }
//        val imageInfo = random?.let { it1 -> viewModel.imageInfoListResults.value?.get(it1) }
        val imageInfo = viewModel.imageInfoListResults.value?.get(viewModel.currentIndex++)
        viewModel.updateConfig()
        viewModel.startTime = System.currentTimeMillis()
        setData(imageInfo)

    }

    fun setData(imageInfo: ImageInfo?) {
        imageInfo?.downloadUrl?.let { it1 -> loadNewImageUsingGlide(it1) }
        imageInfo?.id?.let { it1 -> binding.txtImageId.text = it1 }
        imageInfo?.width?.let { it1 -> binding.txtImageWidth.text = it1.toString() + "px" }
        imageInfo?.height?.let { it1 -> binding.txtImageHeight.text = it1.toString() + "px" }
        imageInfo?.author?.let { it1 -> binding.txtAuthor.text = it1 }
    }
}