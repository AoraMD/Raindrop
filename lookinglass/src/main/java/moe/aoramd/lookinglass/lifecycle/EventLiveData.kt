package moe.aoramd.lookinglass.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 用于收发单次数据的 LiveData，其值每一次更改都将重新置为 null.
 * EventLiveData 不可用于收发可空数据.
 *
 * 应用示例，因 ViewModel 中不应持有 LifecycleOwner 的引用，因此可通过 EventLiveData 向 LiveData Observer 发送信息
 *
 * ```Kotlin
 * /* ViewModel */
 * private val _event = EventLiveData<String>()
 * val event: LiveData<String> = _event
 *
 * fun showLogOnActivity() {
 *     // 向 Observer 推送信息十次
 *     for(i in 1.10) {
 *         _event.value = "This is a message."
 *     }
 * }
 *
 * /* Activity */
 * viewModel.event.observe(this, Observer {
 *     Log.d("Activity", it)    // 该 Log 将被打印十次
 * })
 * ```
 *
 * EventLiveData 一般用于非粘性数据的收发
 *
 * @author M.D.
 * @version 1
 */
class EventLiveData<T> : MutableLiveData<T>() {
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer {
            if (it != null) {
                observer.onChanged(it)
                value = null
            }
        })
    }
}