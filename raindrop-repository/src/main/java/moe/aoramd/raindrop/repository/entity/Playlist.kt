package moe.aoramd.raindrop.repository.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import moe.aoramd.raindrop.repository.Tags

@Entity(tableName = "playlist")
@Parcelize
data class Playlist(
    @PrimaryKey
    val id: Long,
    val accountId: Long,
    val title: String,
    val coverUrl: String,
    val trackCount: Int
) : Parcelable