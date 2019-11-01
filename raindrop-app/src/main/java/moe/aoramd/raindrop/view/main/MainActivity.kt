package moe.aoramd.raindrop.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.ActivityMainBinding
import moe.aoramd.raindrop.view.base.bar.BarControlActivity
import moe.aoramd.raindrop.view.base.bar.BarControlViewModel
import moe.aoramd.raindrop.view.search.SearchHostActivity

/**
 *  raindrop main interface
 *
 *  application launch activity
 *
 *  @author M.D.
 *  @version dev 1
 */
class MainActivity : BarControlActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override val barController: BarControlViewModel by lazy { viewModel }

    // override functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // initialize data binding
        binding.apply {

            // lifecycle
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.viewModel

            // initialize toolbar
            setSupportActionBar(toolbar)

            // link pager and tabs
            pager.adapter =
                MainPagerAdapter(supportFragmentManager)
            tabs.setupWithViewPager(pager)
        }

        // attach music bar
        attachMusicBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_tool_search -> toolbarOptionSearch()
        }
        return super.onOptionsItemSelected(item)
    }

    // toolbar options

    private fun toolbarOptionSearch() {
        startActivity(Intent(this, SearchHostActivity::class.java))
    }
}
