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
        val logoutAccount = Account(
            Tags.OFFLINE_ID, Tags.OFFLINE_TAG,
            Tags.OFFLINE_TAG,
            Tags.OFFLINE_TAG, Tags.OFFLINE_TAG
        )

        val loadingAccount = Account(
            Tags.LOADING_ID, Tags.LOADING_TAG,
            Tags.LOADING_TAG,
            Tags.LOADING_TAG, Tags.LOADING_TAG
        )
    }
}