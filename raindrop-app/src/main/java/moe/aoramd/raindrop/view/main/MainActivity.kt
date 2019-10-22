package moe.aoramd.raindrop.view.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.list.MainPagerAdapter
import moe.aoramd.raindrop.databinding.ActivityMainBinding
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.raindrop.view.play.PlayActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    // media component
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            try {
                viewModel.service?.removePlayingListener(this@MainActivity.toString())
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
                    this@MainActivity.toString(),
                    viewModel.playListener
                )
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            viewModel.controller =
                MediaControllerCompat(this@MainActivity, viewModel.service!!.sessionToken())
            viewModel.controller?.registerCallback(viewModel.controllerCallback)
        }
    }

    // override functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.musicBar.activity = this
        binding.musicBar.viewModel = viewModel
        binding.musicBar.lifecycleOwner = this

        // initialize toolbar
        setSupportActionBar(binding.toolbar)

        // link pager and tabs
        binding.pager.adapter =
            MainPagerAdapter(supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.pager)

        // event listener
        viewModel.event.observe(this, Observer {
            when (it) {
                // view events
                MainViewModel.EVENT_OPEN_PLAY_INTERFACE -> onOpenPlayInterfaceEvent()
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
        viewModel.controller?.unregisterCallback(viewModel.controllerCallback)
        unbindService(serviceConnection)
    }


    // event operations
    private fun onOpenPlayInterfaceEvent() = PlayActivity.start(this)
}
