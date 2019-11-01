package moe.aoramd.raindrop.repository.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "play_record",
    primaryKeys = ["song"],
    foreignKeys = [
        ForeignKey(
            entity = SongMeta::class,
            parentColumns = ["id"],
            childColumns = ["song"]
        )
    ]
)
data class PlayRecord(
    val playTime: Long,
    val song: Long
)