package moe.aoramd.raindrop.view.play

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.ActivityPlayBinding
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.raindrop.view.base.player.PlayerControlActivity
import moe.aoramd.raindrop.view.base.player.PlayerControlViewModel

/**
 *  music play interface activity
 *
 *  @author M.D.
 *  @version dev 1
 */
class PlayActivity : PlayerControlActivity() {

    companion object {
        fun start(activity: FragmentActivity) {
            val intent = Intent(activity, PlayActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityPlayBinding

    private val viewModel: PlayViewModel by viewModels()
    override val playerController: PlayerControlViewModel by lazy { viewModel }

    private val adapter = PlayPlayingAdapter(this)

    // override functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_play)

        // initialize data binding
        binding.apply {

            // lifecycle
            lifecycleOwner = this@PlayActivity
            viewModel = this@PlayActivity.viewModel

            // playing list
            adapter = this@PlayActivity.adapter
            layoutManager =
                LinearLayoutManager(this@PlayActivity, LinearLayoutManager.VERTICAL, false)
        }

        // event listener
        viewModel.event.observe(this, Observer {
            when (it) {

                // service events
                PlayService.EVENT_LOAD_SONG_FAILED -> onLoadSongFailedEvent()
            }
        })
    }

    // event operations

    private fun onLoadSongFailedEvent() {
        Snackbar.make(binding.root, R.string.snackbar_load_song_failed, Snackbar.LENGTH_SHORT)
            .show()
    }

    // item click listeners
    internal val rootClickListener: (index: Int) -> Unit = { index ->
        viewModel.selectSong(index)
    }

    internal val operationClickListener: (view: View, index: Int) -> Unit = { view, index ->
        // todo not implement
    }
}
