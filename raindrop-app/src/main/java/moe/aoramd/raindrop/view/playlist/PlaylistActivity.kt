package moe.aoramd.raindrop.view.playlist

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.binding.BindingBaseAdapter
import moe.aoramd.raindrop.adapter.list.PlaylistListAdapter
import moe.aoramd.raindrop.databinding.ActivityPlaylistBinding
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.source.MusicSource
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.raindrop.view.play.PlayActivity

class PlaylistActivity : AppCompatActivity() {

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
        PlaylistViewModel.PlaylistViewModelFactory(intent.getParcelableExtra(INTENT_PLAYLIST)!!)
    }

    // media component
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.service = null
            viewModel.controller = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            viewModel.service = IPlayService.Stub.asInterface(service)
            viewModel.controller =
                MediaControllerCompat(this@PlaylistActivity, viewModel.service!!.sessionToken())
        }
    }


    // override functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // song list
        binding.adapter = PlaylistListAdapter(this)
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
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, PlayService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
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
                inflate(R.menu.playlist_item_popup)
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
