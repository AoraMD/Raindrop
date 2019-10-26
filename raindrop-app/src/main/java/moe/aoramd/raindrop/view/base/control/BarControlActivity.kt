package moe.aoramd.raindrop.view.base.control

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.WidgetMusicBarBinding
import moe.aoramd.raindrop.view.base.bind.PlayerBindActivity
import moe.aoramd.raindrop.view.base.bind.PlayerBindViewModel
import moe.aoramd.raindrop.view.play.PlayActivity

abstract class BarControlActivity : PlayerBindActivity() {

    private lateinit var barBinding: WidgetMusicBarBinding

    override val binder: PlayerBindViewModel by lazy { barController }

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