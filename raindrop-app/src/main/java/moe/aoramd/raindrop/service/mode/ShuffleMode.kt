package moe.aoramd.raindrop.service.mode

interface ShuffleMode {

    companion object {
        const val INDEX_NONE = -1
    }

    val tag: Int

    fun next(size: Int, currentIndex: Int): Int
    fun previous(size: Int, currentIndex: Int): Int
}