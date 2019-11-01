package moe.aoramd.raindrop.service.mode

/**
 *  an interface to control song switching
 *
 *  @author M.D.
 *  @version dev 1
 */
interface ShuffleMode {

    companion object {
        const val INDEX_NONE = -1
    }

    val tag: Int

    fun next(size: Int, currentIndex: Int): Int
    fun previous(size: Int, currentIndex: Int): Int

    // switch after current song is finished
    fun nextAuto(size: Int, currentIndex: Int): Int = next(size, currentIndex)
}