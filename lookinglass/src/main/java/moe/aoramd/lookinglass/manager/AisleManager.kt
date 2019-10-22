package moe.aoramd.lookinglass.manager

import android.os.Looper
import android.os.Handler
import java.util.concurrent.Executors

/**
 * 线程调度工具
 *
 * @author M.D.
 * @version 1
 */
object AisleManager {
    @JvmStatic
    private val mainHandler = Handler(Looper.getMainLooper())

    @JvmStatic
    private val ioExecutor = Executors.newCachedThreadPool()

    /**
     * 将 Runnable 推送到界面更新线程运行.
     *
     * 应用示例
     *
     * ```Kotlin
     * AisleManager.main(Runnable {
     *     textView.text = sampleString
     * })
     * ```
     *
     * @param runnable 需要在界面更新线程运行的 Runnable 实现
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun main(runnable: Runnable) {
        mainHandler.post(runnable)
    }

    /**
     * 将 Runnable 推送到后台线程运行.
     *
     * 后台线程实现见 [Executors.newCachedThreadPool]
     *
     * 应用示例
     *
     * ```Kotlin
     * AisleManager.io(Runnable {
     *     networkRequest()
     * })
     * ```
     *
     * @param runnable 需要在后台线程运行的 Runnable 实现
     *
     * @author 1
     * @since 1
     */
    @JvmStatic
    fun io(runnable: Runnable) {
        ioExecutor.execute(runnable)
    }
}