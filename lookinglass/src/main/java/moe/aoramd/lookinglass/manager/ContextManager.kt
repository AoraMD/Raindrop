package moe.aoramd.lookinglass.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat

/**
 * Android Context 管理器
 *
 * 通过将 Context 注册到 ContextManager 中以实现对 Context 的全局调用.
 *
 * **注意可能导致的内存泄漏问题**
 *
 * 应用示例，在 Application 对应的生命周期中注册与取消注册 Context
 *
 * ```
 * class MyApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         ContextManager.registerContext(this) // 注册 Context
 *     }
 *
 *     override fun onTerminate() {
 *         ContextManager.releaseContext()  // 取消注册 Context
 *         super.onTerminate()
 *     }
 * }
 * ```
 *
 * 由于 ContextManager 作为对象（单例），因此可在全局通过 ContextManager 获取资源
 *
 * ```
 * class Repository {
 *     fun aFunctionNeedContext() {
 *         val context = ContextManager.context
 *         // ...
 *     }
 * }
 * ```
 *
 * @author M.D.
 * @version 1
 */
@SuppressLint("StaticFieldLeak")
object ContextManager {

    @JvmStatic
    private var con: Context? = null

    /**
     * 维护的 Context 实例
     *
     * @exception IllegalStateException 当无 Context 注册时抛出该异常
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    val context: Context
        get() = con ?: throw IllegalStateException("Context has not register.")

    /**
     * 注册 Context
     *
     * @param context 注册到管理器的 Context 实例
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun registerContext(context: Context) {
        con = context
    }

    /**
     * 取消注册 Context
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun releaseContext() {
        con = null
    }

    /**
     * 通过注册的 Context 获取字符串资源
     *
     * @param resId 资源 ID
     * @return 获取的字符串资源
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun resourceString(resId: Int): String = context.resources.getString(resId)

    /**
     * 通过注册的 Context 获取色彩资源
     *
     * @param resId 资源 ID
     * @return 获取的色彩资源
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun resourceColor(resId: Int): Int = ContextCompat.getColor(context, resId)

    /**
     * 通过注册的 Context 获取 SharedPreferences 资源
     *
     * @param name SharedPreferences 文件名
     * @param mode 操作模式
     * @return 获取的 SharedPreferences 实例
     *
     * @see [SharedPreferences]
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun sharedPreferences(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences =
        context.getSharedPreferences(name, mode)
}