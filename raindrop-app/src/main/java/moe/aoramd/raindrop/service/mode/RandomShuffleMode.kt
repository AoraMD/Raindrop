package moe.aoramd.raindrop.service.mode

import kotlin.random.Random

/**
 *  switching playing song randomly in this mode
 *
 *  @author M.D.
 *  @version dev 1
 */
object RandomShuffleMode : ShuffleMode {

    override val tag: Int = 0xb1ee80a

    override fun next(size: Int, currentIndex: Int): Int = random(size)

    override fun previous(size: Int, currentIndex: Int): Int = random(size)

    private fun random(size: Int): Int =
        if (size <= 0) ShuffleMode.INDEX_NONE
        else Random.nextInt(0, size)
}