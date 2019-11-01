package moe.aoramd.raindrop.view.profile.binding

import android.widget.TextView
import androidx.databinding.*
import moe.aoramd.lookinglass.manager.ContextManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.repository.entity.Account

object BindingProfileAdapter {

    @JvmStatic
    @BindingAdapter("nickname")
    fun setNickname(textView: TextView, text: String) {
        textView.text = when (text) {
            Account.LOADING_TAG -> ContextManager.resourceString(R.string.app_loading)
            Account.OFFLINE_TAG -> ContextManager.resourceString(R.string.profile_click_login)
            else -> text
        }
    }

    @JvmStatic
    @BindingAdapter("signature")
    fun setSignature(textView: TextView, text: String) {
        textView.text = when (text) {
            Account.LOADING_TAG -> ContextManager.resourceString(R.string.app_loading)
            Account.OFFLINE_TAG -> ContextManager.resourceString(R.string.profile_empty_status)
            else -> text
        }
    }
}