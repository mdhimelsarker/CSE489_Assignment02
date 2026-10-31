package com.example.myapplication

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myapplication.databinding.FragmentImageScaleBinding

class ImageScaleFragment : Fragment() {

    private var _binding: FragmentImageScaleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentImageScaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        val initialUrl = binding.etImageUrl.text.toString().trim()
        if (initialUrl.isNotEmpty()) {
            loadImage(initialUrl)
        }
    }

    private fun setupListeners() {
        binding.zoomImageView.setOnScaleChangeListener { scale ->
            binding.tvZoomLevel.text = getString(R.string.zoom_level_format, scale)
        }

        binding.btnLoadImage.setOnClickListener {
            val url = binding.etImageUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.err_empty_url), Toast.LENGTH_SHORT).show()
            } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                Toast.makeText(requireContext(), getString(R.string.err_invalid_url), Toast.LENGTH_SHORT).show()
            } else {
                loadImage(url)
            }
        }

        binding.btnResetZoom.setOnClickListener {
            binding.zoomImageView.resetZoom()
        }

        binding.btnRetry.setOnClickListener {
            val url = binding.etImageUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                loadImage(url)
            }
        }
    }

    private fun loadImage(url: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorContainer.visibility = View.GONE
        binding.zoomImageView.visibility = View.INVISIBLE

        Glide.with(this)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    binding.zoomImageView.visibility = View.GONE
                    binding.errorContainer.visibility = View.VISIBLE
                    binding.tvErrorMessage.text = getString(R.string.err_image_load_failed)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    binding.errorContainer.visibility = View.GONE
                    binding.zoomImageView.visibility = View.VISIBLE
                    binding.zoomImageView.resetZoom()
                    return false
                }
            })
            .into(binding.zoomImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
