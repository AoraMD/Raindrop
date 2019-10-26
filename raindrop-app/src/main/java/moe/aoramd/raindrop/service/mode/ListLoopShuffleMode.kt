package moe.aoramd.raindrop.service.mode

object ListLoopShuffleMode : ShuffleMode {

    override val tag: Int = 0x7ee8c6a

    override fun next(size: Int, currentIndex: Int): Int {
        return if (size <= 0) ShuffleMode.INDEX_NONE
        else {
            when (currentIndex) {
                ShuffleMode.INDEX_NONE -> ShuffleMode.INDEX_NONE
                size - 1 -> 0
                else -> currentIndex + 1
            }
        }
    }

    override fun previous(size: Int, currentIndex: Int): Int {
        return if (size <= 0) ShuffleMode.INDEX_NONE
        else {
            when (currentIndex) {
                ShuffleMode.INDEX_NONE -> ShuffleMode.INDEX_NONE
                0 -> size - 1
                else -> currentIndex - 1
            }
        }
    }

}