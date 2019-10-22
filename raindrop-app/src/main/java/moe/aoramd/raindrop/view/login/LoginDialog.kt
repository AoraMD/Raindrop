package moe.aoramd.raindrop.view.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.databinding.DialogLoginBinding
import java.lang.IllegalStateException

class LoginDialog(private val loginListener: (phone: Long, password: String) -> Unit) :
    DialogFragment() {

    private lateinit var binding: DialogLoginBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_login,
                null,
                false
            )

            val builder = AlertDialog.Builder(it)
                .setView(binding.root)
                .setPositiveButton(
                    R.string.login_login
                ) { _, _ ->
                    val phone = binding.phone.text.toString().toLong()
                    val password = binding.password.text.toString()
                    loginListener.invoke(phone, password)
                }
                .setNegativeButton(R.string.login_cancel, null)

            val dialog = builder.create()
            dialog
        } ?: throw IllegalStateException("activity null")
    }
}