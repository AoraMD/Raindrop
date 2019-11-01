package moe.aoramd.raindrop.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.FragmentProfileBinding
import moe.aoramd.raindrop.repository.source.MusicSource
import moe.aoramd.raindrop.view.login.LoginDialog

/**
 *  profile interface fragment
 *
 *  @author M.D.
 *  @version dev 1
 */
class ProfileFragment : Fragment() {

    companion object {
        private const val LOGIN_DIALOG_TAG = "login"
    }

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_profile,
            container,
            false
        )

        // initialize data binding
        binding.apply {
            lifecycleOwner = this@ProfileFragment
            viewModel = this@ProfileFragment.viewModel
        }

        // event listener
        viewModel.event.observe(this, Observer {
            when (it) {

                // source events
                MusicSource.EVENT_NETWORK_ERROR -> onNetworkErrorEvent()
                MusicSource.EVENT_REQUEST_ERROR -> onRequestErrorEvent()

                // view events
                ProfileViewModel.EVENT_LOGIN -> onLoginEvent()
            }
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

    private fun onLoginEvent() {
        activity?.apply {
            LoginDialog(viewModel::login).show(supportFragmentManager, LOGIN_DIALOG_TAG)
        }
    }
}
