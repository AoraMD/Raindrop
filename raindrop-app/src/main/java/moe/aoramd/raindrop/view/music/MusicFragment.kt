package moe.aoramd.raindrop.view.music

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar

import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.FragmentMusicBinding
import moe.aoramd.raindrop.manager.AccountManager
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.source.MusicSource
import moe.aoramd.raindrop.view.playlist.PlaylistActivity
import moe.aoramd.raindrop.view.recent.RecentActivity

class MusicFragment : Fragment() {

    companion object {
        private const val HEADER_RECENT_PLAY_ID = 1L
        private const val HEADER_MUSIC_RADIO_ID = 2L
        private const val HEADER_DOWNLOAD_ID = 3L
        private val listHeaders = listOf(
            MusicAdapter.Companion.MusicHeader(
                HEADER_RECENT_PLAY_ID,
                R.string.music_recent_play,
                R.drawable.ic_history
            ),
            MusicAdapter.Companion.MusicHeader(
                HEADER_MUSIC_RADIO_ID,
                R.string.music_radio,
                R.drawable.ic_radio
            ),
            MusicAdapter.Companion.MusicHeader(
                HEADER_DOWNLOAD_ID,
                R.string.music_download,
                R.drawable.ic_download
            )
        )
    }

    private lateinit var binding: FragmentMusicBinding

    private val viewModel: MusicViewModel by viewModels()


    private val adapter = MusicAdapter(this)

    init {
        adapter.headers = listHeaders
    }

    // override functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_music,
            container,
            false
        )

        // initialize data binding
        binding.apply {
            // lifecycle
            viewModel = this@MusicFragment.viewModel
            lifecycleOwner = this@MusicFragment

            // playlist list
            adapter = this@MusicFragment.adapter
            layoutManager =
                LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        }


        // event listener
        viewModel.event.observe(this, Observer {
            when (it) {
                // source events
                MusicSource.EVENT_NETWORK_ERROR -> onNetworkErrorEvent()
                MusicSource.EVENT_REQUEST_ERROR -> onRequestErrorEvent()
            }
        })

        // account update listener
        AccountManager.accountLiveData.observe(this, Observer {
            viewModel.refresh(true)
        })

        return binding.root
    }

    // event operations
    private fun onNetworkErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_network_error, Snackbar.LENGTH_SHORT).show()
    }

    private fun onRequestErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_network_error, Snackbar.LENGTH_SHORT).show()
    }

    // header item click listeners

    // item click listeners
    internal val headerItemClickListener: (itemId: Long) -> Unit = { itemId ->
        when (itemId) {
            HEADER_RECENT_PLAY_ID -> {
                RecentActivity.start(activity!!)
            }
        }
    }

    internal val playlistItemClickListener: (playlist: Playlist) -> Unit = { playlist ->
        PlaylistActivity.start(activity!!, playlist)
    }
}
