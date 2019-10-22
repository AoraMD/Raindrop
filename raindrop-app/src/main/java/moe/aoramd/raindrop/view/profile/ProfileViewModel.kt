package moe.aoramd.raindrop.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import moe.aoramd.lookinglass.lifecycle.EventLiveData
import moe.aoramd.raindrop.manager.AccountManager
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Account

class ProfileViewModel : ViewModel() {

    companion object {
        // view event messages
        internal const val EVENT_LOGIN = "#_Profile_login"
        internal const val EVENT_LOGOUT = "#_Profile_logout"
    }

    val updating = MutableLiveData<Boolean>()

    private val _event = EventLiveData<String>()
    val event: LiveData<String> = _event

    val account: LiveData<Account> = AccountManager.accountLiveData

    init {
        updating.value = false
        refresh()
    }

    fun refresh() {
        updating.value = true
        RaindropRepository.updateLoginState(viewModelScope) {
            if (!it)
                AccountManager.accountLiveData.value = Account.logoutAccount
            updating.value = false
        }
    }

    fun changeAccount() {
        if (AccountManager.account == Account.loadingAccount)
            return
        when (AccountManager.account) {
            Account.logoutAccount -> _event.value = EVENT_LOGIN
            else -> _event.value = EVENT_LOGOUT
        }
    }

    internal fun login(phone: Long, password: String) {
        updating.value = true
        RaindropRepository.login(
            viewModelScope,
            phone,
            password,
            { AccountManager.accountLiveData.value = it },
            { errorMsg -> _event.value = errorMsg },
            {
                updating.value = false
            }
        )
    }
}