package moe.aoramd.raindrop.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song")
data class SongMeta(
    @PrimaryKey
    val id: Long,
    val name: String,
    val authors: List<Long>,
    val album: Long
)