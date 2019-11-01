package moe.aoramd.raindrop.view.base.bar

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.WidgetMusicBarBinding
import moe.aoramd.raindrop.view.base.player.PlayerControlActivity
import moe.aoramd.raindrop.view.base.player.PlayerControlViewModel
import moe.aoramd.raindrop.view.play.PlayActivity

/**
 *  activity with a music bar at the bottom of interface
 *
 *  @author M.D.
 *  @version dev 1
 */
abstract class BarControlActivity : PlayerControlActivity() {

    // music bar data binding
    private lateinit var barBinding: WidgetMusicBarBinding

    // media play service controllable view model
    override val playerController: PlayerControlViewModel by lazy { barController }

    // music bar controllable view model
    protected abstract val barController: BarControlViewModel

    // call at the end of onCreate function to attach music bar at the top of interface
    protected fun attachMusicBar() {

        val contentView = findViewById<FrameLayout>(android.R.id.content)

        barBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.widget_music_bar,
            contentView,
            false
        )
        barBinding.apply {
            lifecycleOwner = this@BarControlActivity
            controller = barController
            setLongClickListener {
                PlayActivity.start(this@BarControlActivity)
                true
            }
        }

        contentView.addView(barBinding.root)
    }
}