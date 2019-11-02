package moe.aoramd.raindrop.view.recent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.ActivityRecentBinding
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.view.base.bar.BarControlActivity
import moe.aoramd.raindrop.view.base.bar.BarControlViewModel
import moe.aoramd.raindrop.view.base.list.SwipeToDeleteCallback

/**
 *  recent interface activity
 *
 *  @author M.D.
 *  @version dev 1
 */
class RecentActivity : BarControlActivity() {

    companion object {
        fun start(activity: FragmentActivity) =
            activity.startActivity(Intent(activity, RecentActivity::class.java))
    }

    private lateinit var binding: ActivityRecentBinding

    private val viewModel: RecentViewModel by viewModels()
    override val barController: BarControlViewModel by lazy { viewModel }

    private val adapter = RecentAdapter(this)

    // recent list swipe to delete
    private val swipeListener: (index: Int) -> Unit = { index ->
        viewModel.removeRecentIndex(index)
    }

    private val swipeHandler = SwipeToDeleteCallback(swipeListener)
    private val itemTouchHelper = ItemTouchHelper(swipeHandler)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recent)

        // initialize data binding
        binding.apply {

            // initialize toolbar
            setSupportActionBar(toolbar)

            // play record list
            adapter = this@RecentActivity.adapter
            layoutManager =
                LinearLayoutManager(this@RecentActivity, LinearLayoutManager.VERTICAL, false)
            itemTouchHelper.attachToRecyclerView(list)
        }

        // paged list data
        viewModel.songs.observe(this, Observer { adapter.submitList(it) })

        // attach music bar
        attachMusicBar()
    }

    // item click listeners

    internal val rootClickListener: (song: Song) -> Unit = { song ->
        // todo not implement
    }

    internal val operationClickListener: (view: View, song: Song) -> Unit = { _, song ->
        // todo not implement
    }
}
