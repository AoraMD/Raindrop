package moe.aoramd.raindrop.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import moe.aoramd.lookinglass.lifecycle.EventLiveData
import moe.aoramd.raindrop.manager.AccountManager
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Account

/**
 *  profile interface view model
 *
 *  @author M.D.
 *  @version dev 1
 */
class ProfileViewModel : ViewModel() {

    companion object {
        // view event messages
        internal const val EVENT_LOGIN = "#_Profile_login"
        internal const val EVENT_LOGOUT = "#_Profile_logout"
    }

    // event
    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    // account
    val account: LiveData<Account> = AccountManager.accountLiveData

    // view : is list refreshing
    val refreshing = MutableLiveData<Boolean>()

    init {
        refreshing.value = false

        refresh()
    }

    fun refresh() {
        refreshing.value = true
        RaindropRepository.updateLoginState(viewModelScope) {
            if (!it)
                AccountManager.account = Account.offline
            refreshing.value = false
        }
    }

    fun changeAccount() {
        if (AccountManager.account == Account.loading)
            return
        when (AccountManager.account) {
            Account.offline -> _event.value = EVENT_LOGIN
            else -> _event.value = EVENT_LOGOUT
        }
    }

    internal fun login(phone: Long, password: String) {
        refreshing.value = true

        RaindropRepository.login(
            viewModelScope,
            phone,
            password,
            success = { account ->
                AccountManager.account = account
            },
            error = { errorMsg ->
                _event.value = errorMsg
            },
            complete = {
                refreshing.value = false
            }
        )
    }
}