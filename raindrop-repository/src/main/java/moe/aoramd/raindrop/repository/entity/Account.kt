package moe.aoramd.raindrop.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import moe.aoramd.raindrop.repository.Tags

@Entity(tableName = "account")
data class Account(
    @PrimaryKey
    val id: Long,
    val nickname: String,
    val signature: String,
    val avatarUrl: String,
    val backgroundUrl: String
) {
    companion object {
        const val OFFLINE_ID = -10L
        const val OFFLINE_TAG = "#_Account_offline"

        const val LOADING_ID = -20L
        const val LOADING_TAG = "#_Account_loading"

        val offline = Account(
            OFFLINE_ID, OFFLINE_TAG,
            OFFLINE_TAG,
            OFFLINE_TAG, OFFLINE_TAG
        )

        val loading = Account(
            LOADING_ID, LOADING_TAG,
            LOADING_TAG,
            LOADING_TAG, LOADING_TAG
        )
    }
}