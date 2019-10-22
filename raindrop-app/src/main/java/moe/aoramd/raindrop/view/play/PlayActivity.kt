package moe.aoramd.raindrop.view.play

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.binding.BindingBaseAdapter
import moe.aoramd.raindrop.adapter.list.PlayPlayingAdapter
import moe.aoramd.raindrop.databinding.ActivityPlayBinding
import moe.aoramd.raindrop.service.PlayService

class PlayActivity : AppCompatActivity() {

    companion object {
        fun start(activity: FragmentActivity) {
            val intent = Intent(activity, PlayActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityPlayBinding

    private val viewModel: PlayViewModel by viewModels()

    // media component
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            try {
                viewModel.service?.removePlayingListener(this@PlayActivity.toString())
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            viewModel.service = null
            viewModel.controller = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            viewModel.service = IPlayService.Stub.asInterface(service)
            try {
                viewModel.service?.addPlayingListener(
                    this@PlayActivity.toString(),
                    viewModel.playListener
                )
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            viewModel.controller =
                MediaControllerCompat(this@PlayActivity, viewModel.service!!.sessionToken())
            viewModel.controller?.registerCallback(viewModel.controllerCallback)
        }
    }


    // override functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // playing list
        binding.adapter = PlayPlayingAdapter(this)
        binding.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
        viewModel.controller?.unregisterCallback(viewModel.controllerCallback)
        unbindService(serviceConnection)
    }

    // click listener
    val rootClickListener = object : BindingBaseAdapter.IndexClickListener {
        override fun onClick(view: View, index: Int) {
            viewModel.onClickPlayingList(index)
        }
    }
}
