package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.myapplication.databinding.FragmentVideoBinding

@OptIn(UnstableApi::class)
class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null
    private var currentUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUrl = binding.etVideoUrl.text.toString().trim()

        setupListeners()
        if (currentUrl.isNotEmpty()) {
            initializePlayer(currentUrl)
        }
    }

    private fun setupListeners() {
        binding.btnLoadVideo.setOnClickListener {
            val url = binding.etVideoUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.err_empty_url), Toast.LENGTH_SHORT).show()
            } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                Toast.makeText(requireContext(), getString(R.string.err_invalid_url), Toast.LENGTH_SHORT).show()
            } else {
                currentUrl = url
                initializePlayer(currentUrl)
            }
        }

        binding.btnSampleVideo1.setOnClickListener {
            val url = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4"
            binding.etVideoUrl.setText(url)
            currentUrl = url
            initializePlayer(currentUrl)
        }

        binding.btnSampleVideo2.setOnClickListener {
            val url = "https://vjs.zencdn.net/v/oceans.mp4"
            binding.etVideoUrl.setText(url)
            currentUrl = url
            initializePlayer(currentUrl)
        }

        binding.btnSampleVideo3.setOnClickListener {
            val url = "https://www.w3schools.com/html/mov_bbb.mp4"
            binding.etVideoUrl.setText(url)
            currentUrl = url
            initializePlayer(currentUrl)
        }

        binding.btnVideoRetry.setOnClickListener {
            val url = binding.etVideoUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                currentUrl = url
                initializePlayer(currentUrl)
            }
        }
    }

    private fun initializePlayer(url: String) {
        releasePlayer()

        binding.videoProgressBar.visibility = View.VISIBLE
        binding.videoErrorContainer.visibility = View.GONE

        try {
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)

            val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)

            val exoPlayer = ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(mediaSourceFactory)
                .build()

            player = exoPlayer
            binding.playerView.player = exoPlayer

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            binding.videoProgressBar.visibility = View.VISIBLE
                            binding.videoErrorContainer.visibility = View.GONE
                        }
                        Player.STATE_READY -> {
                            binding.videoProgressBar.visibility = View.GONE
                            binding.videoErrorContainer.visibility = View.GONE
                        }
                        Player.STATE_ENDED -> {
                            binding.videoProgressBar.visibility = View.GONE
                        }
                        Player.STATE_IDLE -> {}
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    binding.videoProgressBar.visibility = View.GONE
                    binding.videoErrorContainer.visibility = View.VISIBLE
                    val errorDetail = error.localizedMessage ?: "Network or source error"
                    val errorMsg = getString(R.string.err_video_load_failed) + "\n(" + errorDetail + ")"
                    binding.tvVideoError.text = errorMsg
                }
            })

            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

        } catch (_: Exception) {
            binding.videoProgressBar.visibility = View.GONE
            binding.videoErrorContainer.visibility = View.VISIBLE
            binding.tvVideoError.text = getString(R.string.err_video_load_failed)
        }
    }

    private fun releasePlayer() {
        val p = player
        if (p != null) {
            p.stop()
            p.release()
        }
        player = null
        if (_binding != null) {
            binding.playerView.player = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (player == null && currentUrl.isNotEmpty()) {
            initializePlayer(currentUrl)
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        _binding = null
    }
}
