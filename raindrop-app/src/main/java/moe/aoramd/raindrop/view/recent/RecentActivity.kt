package moe.aoramd.raindrop.view.recent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.ActivityRecentBinding
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.base.bar.BarControlActivity
import moe.aoramd.raindrop.view.base.bar.BarControlViewModel

class RecentActivity : BarControlActivity() {

    companion object {
        fun start(activity: FragmentActivity) =
            activity.startActivity(Intent(activity, RecentActivity::class.java))
    }

    private lateinit var binding: ActivityRecentBinding

    private val viewModel: RecentViewModel by viewModels()

    override val barController: BarControlViewModel by lazy { viewModel }

    private val adapter = RecentAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recent)

        // initialize data binding
        binding.apply {
            adapter = this@RecentActivity.adapter
            layoutManager =
                LinearLayoutManager(this@RecentActivity, LinearLayoutManager.VERTICAL, false)
        }

        // initialize toolbar
        setSupportActionBar(binding.toolbar)

        // list data
        viewModel.songs.observe(this, Observer { adapter.submitList(it) })
    }

    // item click listeners
    internal val rootClickListener: (song: Song) -> Unit = { song ->

    }

    internal val operationClickListener: (view: View, song: Song) -> Unit = { _, song ->

    }
}
