package moe.aoramd.raindrop.view.base.bar

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.WidgetMusicBarBinding
import moe.aoramd.raindrop.view.base.player.PlayerControlActivity
import moe.aoramd.raindrop.view.base.player.PlayerControlViewModel
import moe.aoramd.raindrop.view.play.PlayActivity

abstract class BarControlActivity : PlayerControlActivity() {

    private lateinit var barBinding: WidgetMusicBarBinding

    override val playerController: PlayerControlViewModel by lazy { barController }

    protected abstract val barController: BarControlViewModel

    protected fun attachMusicBar() {
        val contentView = findViewById<FrameLayout>(android.R.id.content)
        barBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.widget_music_bar,
            contentView, false
        )
        barBinding.longClickListener = View.OnLongClickListener {
            PlayActivity.start(this)
            true
        }
        barBinding.lifecycleOwner = this
        barBinding.controller = barController
        contentView.addView(barBinding.root)
    }
}