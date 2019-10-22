package moe.aoramd.raindrop.repository.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import moe.aoramd.raindrop.repository.Tags

@Entity(tableName = "album")
@Parcelize
data class Album(
    @PrimaryKey
    val id: Long,
    val name: String,
    val coverUrl: String
) : Parcelable {
    companion object {
        val offline = Album(
            Tags.OFFLINE_ID,
            Tags.OFFLINE_TAG,
            Tags.OFFLINE_TAG
        )
    }
}