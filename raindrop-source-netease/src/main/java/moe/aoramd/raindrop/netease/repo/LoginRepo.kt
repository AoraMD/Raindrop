package moe.aoramd.raindrop.netease.repo

import com.google.gson.annotations.SerializedName
import moe.aoramd.raindrop.repository.entity.Account

data class LoginRepo(

    val code: Int,
    val msg: String = "",

    @SerializedName("profile")
    val account: Account
)