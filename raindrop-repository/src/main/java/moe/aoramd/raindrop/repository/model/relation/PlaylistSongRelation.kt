package moe.aoramd.raindrop.repository.model.relation

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.entity.SongMeta

@Entity(
    tableName = "playlist_song_relation",
    primaryKeys = ["playlist", "song"],
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlist"]
        ),
        ForeignKey(
            entity = SongMeta::class,
            parentColumns = ["id"],
            childColumns = ["song"]
        )
    ]
)
data class PlaylistSongRelation(
    val playlist: Long,
    val song: Long
)