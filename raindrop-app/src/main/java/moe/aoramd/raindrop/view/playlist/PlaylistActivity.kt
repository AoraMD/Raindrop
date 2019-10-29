package moe.aoramd.raindrop.view.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.binding.BindingBaseAdapter
import moe.aoramd.raindrop.adapter.list.PlaylistAdapter
import moe.aoramd.raindrop.databinding.ActivityPlaylistBinding
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.source.MusicSource
import moe.aoramd.raindrop.view.base.control.BarControlActivity
import moe.aoramd.raindrop.view.base.control.BarControlViewModel
import moe.aoramd.raindrop.view.play.PlayActivity

class PlaylistActivity : BarControlActivity() {

    companion object {
        private const val INTENT_PLAYLIST = "playlist"

        fun start(context: Context, playlist: Playlist) {
            val intent = Intent(context, PlaylistActivity::class.java)
            intent.putExtra(INTENT_PLAYLIST, playlist)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityPlaylistBinding

    private val viewModel: PlaylistViewModel by viewModels {
        PlaylistViewModel.Factory(intent.getParcelableExtra(INTENT_PLAYLIST)!!)
    }

    override val barController: BarControlViewModel by lazy { viewModel }

    // override functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // song list
        binding.adapter = PlaylistAdapter(this)
        binding.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // event listener
        viewModel.event.observe(this, Observer {
            when (it) {
                // source events
                MusicSource.EVENT_NETWORK_ERROR -> onNetworkErrorEvent()
                MusicSource.EVENT_REQUEST_ERROR -> onRequestErrorEvent()
                // repository events
                RaindropRepository.MSG_FILE_EXIST_ERROR -> onFileExistErrorEvent()
                RaindropRepository.MSG_FILE_DOWNLOADING_ERROR -> onDownloadingErrorEvent()
                RaindropRepository.MSG_DOWNLOAD_SUCCESSFULLY -> onDownloadSuccessfullyEvent()
            }
        })

        // attach music bar
        attachMusicBar()
    }

    // event operations
    private fun onNetworkErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_network_error, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun onRequestErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_network_error, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun onFileExistErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_file_exist_error, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun onDownloadingErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_downloading_error, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun onDownloadSuccessfullyEvent() {
        Snackbar.make(binding.root, R.string.snackbar_download_successfully, Snackbar.LENGTH_SHORT)
            .show()
    }

    // click listener
    val rootClickListener = object : BindingBaseAdapter.IndexClickListener {
        override fun onClick(view: View, index: Int) {
            viewModel.playPlaylist(index.toLong())
            PlayActivity.start(this@PlaylistActivity)
        }
    }

    val operationClickListener = object : BindingBaseAdapter.IndexClickListener {
        override fun onClick(view: View, index: Int) {
            PopupMenu(this@PlaylistActivity, view).apply {
                inflate(R.menu.playlist_item_operation)
                setOnMenuItemClickListener {
                    var result = true
                    when (it.itemId) {
                        R.id.playlist_item_popup_play_as_next -> {
                            if (viewModel.playAsNext(index.toLong()))
                                PlayActivity.start(this@PlaylistActivity)
                        }
                        R.id.playlist_item_popup_download -> {
                            viewModel.download(index.toLong())
                        }
                        else -> result = false
                    }
                    result
                }
                show()
            }
        }
    }
}
