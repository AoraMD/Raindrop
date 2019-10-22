package moe.aoramd.raindrop.repository.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import moe.aoramd.raindrop.repository.Tags

@Parcelize
data class Song(
    val id: Long,
    val name: String,
    val like: Boolean,
    val authors: List<Author>,
    val album: Album
) : Parcelable {
    val authorsName: String
        get() =
            if (authors.isNotEmpty()) {
                val builder = StringBuilder()
                for (index in 0 until (authors.size - 1)) {
                    builder.append(authors[index].name)
                    builder.append(",")
                }
                builder.append(authors.last().name)
                builder.toString()
            } else "No Author"


    companion object {
        val offline = Song(
            Tags.OFFLINE_ID,
            Tags.OFFLINE_TAG,
            false,
            listOf(),
            Album.offline
        )
    }
}