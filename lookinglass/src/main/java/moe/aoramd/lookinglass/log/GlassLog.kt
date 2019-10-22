package moe.aoramd.lookinglass.log

import android.util.Log

/**
 * 日志打印工具
 *
 * 应用示例
 *
 * ```Kotlin
 * GlassLog.d("This is a logcat")
 * ```
 *
 * @author M.D.
 * @version 1
 */
object GlassLog {
    private const val DEFAULT_TAG = "Looking Glass Tag"

    /**
     * 日志模式
     */
    enum class Mode {
        DEBUG, RELEASE
    }

    /**
     * 日志模式
     *
     * 可通过设置 GlassLog 的模式实现不同方式的打印功能.
     *
     * 在 [Mode.RELEASE] 下将不打印任何日志
     */
    @JvmStatic
    var mode: Mode = Mode.DEBUG

    /**
     * 默认日志标签
     *
     * 在未指定的情况下，默认日志标签为 "Looking Glass Tag"
     */
    @JvmStatic
    var defaultTag = DEFAULT_TAG

    /**
     * 打印 Verbose 级别日志
     *
     * @param message 日志信息
     * @param tag 日志标签，命名参数，默认值为默认日志标签
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun v(message: String, tag: String = defaultTag) {
        when (mode) {
            Mode.DEBUG -> Log.v(tag, message)
            Mode.RELEASE -> {}
        }
    }

    /**
     * 打印 Debug 级别日志
     *
     * @param message 日志信息
     * @param tag 日志标签，命名参数，默认值为默认日志标签
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun d(message: String, tag: String = defaultTag) {
        when (mode) {
            Mode.DEBUG -> Log.d(tag, message)
            Mode.RELEASE -> {}
        }
    }

    /**
     * 打印 Warning 级别日志
     *
     * @param message 日志信息
     * @param tag 日志标签，命名参数，默认值为默认日志标签
     *
     * @author M.D.
     * @since 1
     */
    @JvmStatic
    fun w(message: String, tag: String = defaultTag) {
        when (mode) {
            Mode.DEBUG -> Log.w(tag, message)
            Mode.RELEASE -> {}
        }
    }

    /**
     * 打印 Error 级别日志
     *
     * @param message 日志信息
     * @param tag 日志标签，命名参数，默认值为默认日志标签
     */
    @JvmStatic
    fun e(message: String, tag: String = defaultTag) {
        when (mode) {
            Mode.DEBUG -> Log.e(tag, message)
            Mode.RELEASE -> {}
        }
    }
}