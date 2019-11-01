package moe.aoramd.raindrop.service.mode

import java.util.*

/**
 *  managers shuffle modes
 *
 *  @constructor
 *  create new manager instance with default shuffle mode.
 *
 *  @param defaultMode default shuffle mode
 *
 *  @author M.D.
 *  @version dev 1
 */
class ShuffleModeManager(defaultMode: ShuffleMode) {

    private var current = defaultMode
    val mode
        get() = current

    private val queue: Queue<ShuffleMode> = LinkedList<ShuffleMode>()

    fun changeMode() {
        queue.offer(current)
        current = queue.poll()!!
    }

    // add extra shuffle modes
    fun add(mode: ShuffleMode) {
        queue.offer(mode)
    }
}