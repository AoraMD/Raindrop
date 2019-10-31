package moe.aoramd.raindrop.repository.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import moe.aoramd.raindrop.repository.Tags

@Parcelize
data class Song(
    val id: Long,
    val name: String,
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
        val unknown = Song(
            Tags.UNKNOWN_ID,
            Tags.UNKNOWN_TAG,
            listOf(),
            Album.unknown
        )
    }
}