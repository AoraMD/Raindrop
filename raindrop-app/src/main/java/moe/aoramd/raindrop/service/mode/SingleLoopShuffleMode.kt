package moe.aoramd.raindrop.service.mode

object SingleLoopShuffleMode : ShuffleMode {

    override val tag: Int = 0x33aaae0

    override fun next(size: Int, currentIndex: Int): Int = currentIndex

    override fun previous(size: Int, currentIndex: Int): Int = currentIndex
}