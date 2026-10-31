package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
import com.example.myapplication.databinding.FragmentAudioBinding

@OptIn(UnstableApi::class)
class AudioFragment : Fragment() {

    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null
    private var currentUrl: String = ""

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            val p = player
            if (p != null && p.isPlaying) {
                val currentMs = p.currentPosition
                val durationMs = p.duration
                if (durationMs > 0) {
                    binding.audioSeekBar.max = durationMs.toInt()
                    binding.audioSeekBar.progress = currentMs.toInt()
                    binding.tvCurrentTime.text = formatTime(currentMs)
                    binding.tvTotalDuration.text = formatTime(durationMs)
                }
            }
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUrl = binding.etAudioUrl.text.toString().trim()

        setupListeners()
        if (currentUrl.isNotEmpty()) {
            initializePlayer(currentUrl)
        }
    }

    private fun setupListeners() {
        binding.btnLoadAudio.setOnClickListener {
            val url = binding.etAudioUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.err_empty_url), Toast.LENGTH_SHORT).show()
            } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                Toast.makeText(requireContext(), getString(R.string.err_invalid_url), Toast.LENGTH_SHORT).show()
            } else {
                currentUrl = url
                initializePlayer(currentUrl)
            }
        }

        binding.btnAudioPlay.setOnClickListener {
            val p = player
            if (p == null && currentUrl.isNotEmpty()) {
                initializePlayer(currentUrl)
            } else {
                p?.play()
            }
        }

        binding.btnAudioPause.setOnClickListener {
            player?.pause()
        }

        binding.btnAudioStop.setOnClickListener {
            player?.pause()
            player?.seekTo(0)
            binding.audioSeekBar.progress = 0
            binding.tvCurrentTime.text = getString(R.string.time_zero)
            binding.tvAudioStatus.text = "Stopped"
        }

        binding.btnAudioRetry.setOnClickListener {
            val url = binding.etAudioUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                currentUrl = url
                initializePlayer(currentUrl)
            }
        }

        binding.audioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.tvCurrentTime.text = formatTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    player?.seekTo(seekBar.progress.toLong())
                }
            }
        })
    }

    private fun initializePlayer(url: String) {
        releasePlayer()

        binding.audioProgressBar.visibility = View.VISIBLE
        binding.audioErrorContainer.visibility = View.GONE
        binding.tvAudioStatus.text = "Loading audio..."

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

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            binding.audioProgressBar.visibility = View.VISIBLE
                            binding.audioErrorContainer.visibility = View.GONE
                            binding.tvAudioStatus.text = "Buffering audio stream..."
                        }
                        Player.STATE_READY -> {
                            binding.audioProgressBar.visibility = View.GONE
                            binding.audioErrorContainer.visibility = View.GONE
                            val durationMs = exoPlayer.duration
                            if (durationMs > 0) {
                                binding.audioSeekBar.max = durationMs.toInt()
                                binding.tvTotalDuration.text = formatTime(durationMs)
                            }
                            binding.tvAudioStatus.text = if (exoPlayer.isPlaying) "Playing" else "Ready to play"
                        }
                        Player.STATE_ENDED -> {
                            binding.audioProgressBar.visibility = View.GONE
                            binding.tvAudioStatus.text = "Playback completed"
                            binding.audioSeekBar.progress = 0
                            binding.tvCurrentTime.text = getString(R.string.time_zero)
                            handler.removeCallbacks(updateProgressRunnable)
                        }
                        Player.STATE_IDLE -> {}
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        binding.tvAudioStatus.text = "Playing"
                        handler.post(updateProgressRunnable)
                    } else {
                        if (player?.playbackState == Player.STATE_READY) {
                            binding.tvAudioStatus.text = "Paused"
                        }
                        handler.removeCallbacks(updateProgressRunnable)
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    binding.audioProgressBar.visibility = View.GONE
                    binding.audioErrorContainer.visibility = View.VISIBLE
                    binding.tvAudioStatus.text = "Playback error"
                    val errorDetail = error.localizedMessage ?: "Network or source error"
                    val errorMsg = getString(R.string.err_audio_load_failed) + "\n(" + errorDetail + ")"
                    binding.tvAudioError.text = errorMsg
                    handler.removeCallbacks(updateProgressRunnable)
                }
            })

            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()

        } catch (_: Exception) {
            binding.audioProgressBar.visibility = View.GONE
            binding.audioErrorContainer.visibility = View.VISIBLE
            binding.tvAudioStatus.text = "Error loading audio"
            binding.tvAudioError.text = getString(R.string.err_audio_load_failed)
        }
    }

    private fun formatTime(millis: Long): String {
        if (millis <= 0) return "00:00"
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun releasePlayer() {
        handler.removeCallbacks(updateProgressRunnable)
        val p = player
        if (p != null) {
            p.stop()
            p.release()
        }
        player = null
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        _binding = null
    }
}
