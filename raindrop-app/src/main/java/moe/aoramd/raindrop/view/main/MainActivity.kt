package moe.aoramd.raindrop.view.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.list.MainPagerAdapter
import moe.aoramd.raindrop.databinding.ActivityMainBinding
import moe.aoramd.raindrop.view.base.control.BarControlActivity
import moe.aoramd.raindrop.view.base.control.BarControlViewModel

class MainActivity : BarControlActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override val barController: BarControlViewModel by lazy { viewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // initialize toolbar
        setSupportActionBar(binding.toolbar)

        // link pager and tabs
        binding.pager.adapter =
            MainPagerAdapter(supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.pager)

        // attach music bar
        attachMusicBar()
    }
}
