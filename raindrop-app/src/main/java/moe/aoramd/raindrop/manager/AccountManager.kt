package moe.aoramd.raindrop.manager

import androidx.lifecycle.MutableLiveData
import moe.aoramd.raindrop.repository.entity.Account

object AccountManager {

    val accountLiveData = MutableLiveData<Account>()

    val account: Account
        get() = accountLiveData.value ?: throw IllegalStateException("account not initialize")

    init {
        accountLiveData.value = Account.offline
    }
}