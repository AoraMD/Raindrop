package moe.aoramd.raindrop.repository.model.relation

import androidx.room.Entity
import androidx.room.ForeignKey
import moe.aoramd.raindrop.repository.entity.Account
import moe.aoramd.raindrop.repository.entity.Playlist

@Entity(
    tableName = "account_playlist_relation",
    primaryKeys = ["account", "playlist"],
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["account"]
        ),
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlist"]
        )
    ]
)
data class AccountPlaylistRelation(
    val account: Long,
    val playlist: Long
)