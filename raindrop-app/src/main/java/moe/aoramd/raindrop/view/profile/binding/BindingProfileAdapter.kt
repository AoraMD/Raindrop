package moe.aoramd.raindrop.view.profile.binding

import android.widget.TextView
import androidx.databinding.*
import moe.aoramd.lookinglass.manager.ContextManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.repository.Tags
import moe.aoramd.raindrop.repository.entity.Account

/**
 *  profile interface data binding adapter
 *
 *  @author M.D.
 *  @version dev 1
 */
object BindingProfileAdapter {

    @JvmStatic
    @BindingAdapter("nickname")
    fun setNickname(textView: TextView, text: String) {
        textView.text = when (text) {
            Tags.LOADING_TAG -> ContextManager.resourceString(R.string.loading)
            Account.OFFLINE_TAG -> ContextManager.resourceString(R.string.click_to_login)
            else -> text
        }
    }

    @JvmStatic
    @BindingAdapter("signature")
    fun setSignature(textView: TextView, text: String) {
        textView.text = when (text) {
            Tags.LOADING_TAG -> ContextManager.resourceString(R.string.loading)
            Account.OFFLINE_TAG -> ContextManager.resourceString(R.string.empty_status)
            else -> text
        }
    }
}