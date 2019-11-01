package moe.aoramd.raindrop.view.base.player

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

/**
 *  media play service controllable activity
 *
 *  @author M.D.
 *  @version dev 1
 */
abstract class PlayerControlActivity : AppCompatActivity() {

    // media play service controllable view model
    abstract val playerController: PlayerControlViewModel

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            try {
                playerController.removePlayingListenerIfNeed()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            playerController.service = null
            playerController.controller = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playerController.service = IPlayService.Stub.asInterface(service)
            playerController.addPlayingListenerIfNeed()
            playerController.controller =
                MediaControllerCompat(this@PlayerControlActivity, playerController.service!!.sessionToken())
        }
    }

    override fun onStart() {
        super.onStart()

        // bind play service
        bindService(
            Intent(this, PlayService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()

        // unbind play service
        unbindService(serviceConnection)
    }
}