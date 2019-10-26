package moe.aoramd.raindrop.view.base.bind

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import androidx.appcompat.app.AppCompatActivity
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.service.PlayService

abstract class PlayerBindActivity : AppCompatActivity() {

    abstract val binder: PlayerBindViewModel

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            try {
                binder.removePlayingListenerIfNeed()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            binder.service = null
            binder.controller = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder.service = IPlayService.Stub.asInterface(service)
            binder.addPlayingListenerIfNeed()
            binder.controller =
                MediaControllerCompat(this@PlayerBindActivity, binder.service!!.sessionToken())
        }
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
        binder.controller = null
        unbindService(serviceConnection)
    }
}