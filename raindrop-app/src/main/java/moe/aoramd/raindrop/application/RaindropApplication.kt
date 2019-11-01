package moe.aoramd.raindrop.application

import android.app.Application
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import moe.aoramd.lookinglass.log.GlassLog
import moe.aoramd.raindrop.manager.AccountManager
import moe.aoramd.raindrop.netease.NeteaseMusicSource
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.lookinglass.manager.ContextManager

/**
 *  raindrop application
 *
 *  initialize tools and basic data here
 *
 *  @author M.D.
 *  @version dev 1
 */
class RaindropApplication : Application() {

    init {

        // set raindrop source as Netease Music Source
        RaindropRepository.source = NeteaseMusicSource()
    }

    override fun onCreate() {
        super.onCreate()

        // initialize glass logcat
        GlassLog.mode = GlassLog.Mode.DEBUG

        // register context
        ContextManager.registerContext(this)

        // load account
        val job = Job()
        val scope = CoroutineScope(Dispatchers.Main + job)
        RaindropRepository.localAccount(scope) { AccountManager.account = it }

        // start music play service
        startService(Intent(this, PlayService::class.java))
    }

    override fun onTerminate() {

        // release context
        ContextManager.releaseContext()

        super.onTerminate()
    }

}