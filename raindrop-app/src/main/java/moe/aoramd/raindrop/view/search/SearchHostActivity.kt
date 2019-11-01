package moe.aoramd.raindrop.view.search

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.ActivitySearchHostBinding
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.source.MusicSource
import moe.aoramd.raindrop.view.base.bar.BarControlActivity
import moe.aoramd.raindrop.view.base.bar.BarControlViewModel

/**
 *  search interface host activity
 *
 *  @author M.D.
 *  @version dev 1
 */
class SearchHostActivity : BarControlActivity() {

    private lateinit var binding: ActivitySearchHostBinding

    private val viewModel: SearchHostViewModel by viewModels()
    override val barController: BarControlViewModel by lazy { viewModel }

    // search view query listener
    private val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            viewModel.submit()
            query?.apply { refreshSearchList(this) }
            binding.search.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean = false
    }

    // override functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_host)

        // initialize data binding
        binding.apply {
            lifecycleOwner = this@SearchHostActivity
            viewModel = this@SearchHostActivity.viewModel
        }

        // initialize search view
        binding.search.apply {
            setOnQueryTextListener(queryListener)
            onActionViewExpanded()
        }

        // event listener
        viewModel.event.observe(this, Observer {
            when (it) {

                // source events
                MusicSource.EVENT_NETWORK_ERROR -> onNetworkErrorEvent()
                MusicSource.EVENT_REQUEST_ERROR -> onRequestErrorEvent()

                // repository events
                RaindropRepository.MSG_FILE_EXIST_ERROR -> onFileExistErrorEvent()
                RaindropRepository.MSG_FILE_DOWNLOADING_ERROR -> onDownloadingErrorEvent()
                RaindropRepository.MSG_DOWNLOAD_SUCCESSFULLY -> onDownloadSuccessfullyEvent()
            }
        })

        // attach music bar
        attachMusicBar()
    }

    // private functions

    private fun refreshSearchList(keywords: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.search_content, SearchListFragment(keywords))
            .commit()
    }

    // event operations

    private fun onNetworkErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_network_error, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun onRequestErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_network_error, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun onFileExistErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_file_exist_error, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun onDownloadingErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_downloading_error, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun onDownloadSuccessfullyEvent() {
        Snackbar.make(binding.root, R.string.snackbar_download_successfully, Snackbar.LENGTH_SHORT)
            .show()
    }
}
