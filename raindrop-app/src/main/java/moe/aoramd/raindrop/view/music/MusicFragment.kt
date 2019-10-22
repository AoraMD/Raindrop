package moe.aoramd.raindrop.view.music

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar

import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.list.MusicListAdapter
import moe.aoramd.raindrop.databinding.FragmentMusicBinding
import moe.aoramd.raindrop.manager.AccountManager
import moe.aoramd.raindrop.repository.source.MusicSource

class MusicFragment : Fragment() {

    private lateinit var binding: FragmentMusicBinding

    private val viewModel: MusicViewModel by viewModels()

    // override functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_music,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // playlist list
        binding.adapter = MusicListAdapter(activity!!)
        binding.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)

        // event listener
        viewModel.event.observe(this, Observer {
            when (it) {
                // source events
                MusicSource.EVENT_NETWORK_ERROR -> onNetworkErrorEvent()
                MusicSource.EVENT_REQUEST_ERROR -> onRequestErrorEvent()
            }
        })

        // account update listener
        AccountManager.accountLiveData.observe(this, Observer {
            viewModel.refresh(true)
        })

        return binding.root
    }

    // event operations
    private fun onNetworkErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_network_error, Snackbar.LENGTH_SHORT).show()
    }

    private fun onRequestErrorEvent() {
        Snackbar.make(binding.root, R.string.snackbar_network_error, Snackbar.LENGTH_SHORT).show()
    }
}
