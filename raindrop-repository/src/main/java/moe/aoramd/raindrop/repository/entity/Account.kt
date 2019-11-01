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
        const val OFFLINE_ID: Long = -0x1ac0ff25f09d5cf5
        const val OFFLINE_TAG: String = "&Account.offline"

        val offline = Account(
            OFFLINE_ID, OFFLINE_TAG,
            OFFLINE_TAG,
            OFFLINE_TAG, OFFLINE_TAG
        )

        val loading = Account(
            Tags.LOADING_ID, Tags.LOADING_TAG,
            Tags.LOADING_TAG,
            Tags.LOADING_TAG, Tags.LOADING_TAG
        )
    }
}