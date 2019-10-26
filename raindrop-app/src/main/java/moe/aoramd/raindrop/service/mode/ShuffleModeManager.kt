package moe.aoramd.raindrop.service.mode

import java.util.*

class ShuffleModeManager(defaultMode: ShuffleMode) {

    var mode: ShuffleMode = defaultMode

    private val queue: Queue<ShuffleMode> = LinkedList<ShuffleMode>()

    fun changeMode() {
        queue.offer(mode)
        mode = queue.poll()!!
    }

    fun add(mode: ShuffleMode) {
        queue.offer(mode)
    }
}