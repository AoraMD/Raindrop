package moe.aoramd.raindrop.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import moe.aoramd.raindrop.repository.entity.Account

/**
 *  manages raindrop account and related data
 *
 *  @author M.D.
 *  @version dev 1
 */
object AccountManager {

    private val accountLiveDataMutable = MutableLiveData<Account>()
    val accountLiveData: LiveData<Account> = accountLiveDataMutable

    var account: Account
        get() = accountLiveData.value
            ?: throw IllegalStateException("account not initialize")
        set(value) {
            accountLiveDataMutable.value = value
        }

    init {
        accountLiveDataMutable.value = Account.offline
    }
}