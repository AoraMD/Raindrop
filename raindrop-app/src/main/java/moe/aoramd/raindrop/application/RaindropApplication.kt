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

class RaindropApplication : Application() {

    init {
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
        RaindropRepository.localAccount(scope) { AccountManager.accountLiveData.value = it }

        startService(Intent(this, PlayService::class.java))
    }

    override fun onTerminate() {
        // RELEASE context
        ContextManager.releaseContext()

        super.onTerminate()
    }

}