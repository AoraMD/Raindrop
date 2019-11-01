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

class MainActivity : BarControlActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override val barController: BarControlViewModel by lazy { viewModel }

    // Override Functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Initialize Toolbar
        setSupportActionBar(binding.toolbar)

        // Link Pager and Tabs
        binding.pager.adapter =
            MainPagerAdapter(supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.pager)

        // Attach Music Bar
        attachMusicBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_tool_search -> openSearchInterface()
        }
        return super.onOptionsItemSelected(item)
    }

    // Private Functions
    private fun openSearchInterface() {
        startActivity(Intent(this, SearchHostActivity::class.java))
    }
}
