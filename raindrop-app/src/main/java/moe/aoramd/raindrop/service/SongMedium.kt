package moe.aoramd.raindrop.service

import android.os.Parcel
import android.os.Parcelable
import moe.aoramd.raindrop.repository.entity.Album
import moe.aoramd.raindrop.repository.entity.Author
import moe.aoramd.raindrop.repository.entity.Song

/**
 *  medium carrying song data for crossing processes
 *
 *  @property id song id
 *  @property name song name
 *  @property authorIds list of author id
 *  @property authorNames list of author name
 *  @property albumId album id
 *  @property albumName album name
 *  @property albumCoverUrl album cover url
 *
 *  @author M.D.
 *  @version 1
 */
data class SongMedium(
    val id: Long,
    val name: String,
    val authorIds: List<Long>,
    val authorNames: List<String>,
    val albumId: Long,
    val albumName: String,
    val albumCoverUrl: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: PARCEL_ERROR_TAG,
        parcel.createLongArray()?.toList() ?: listOf(),
        parcel.createStringArrayList() ?: listOf(),
        parcel.readLong(),
        parcel.readString() ?: PARCEL_ERROR_TAG,
        parcel.readString() ?: PARCEL_ERROR_TAG
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeLongArray(authorIds.toLongArray())
        parcel.writeStringList(authorNames)
        parcel.writeLong(albumId)
        parcel.writeString(albumName)
        parcel.writeString(albumCoverUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        private const val PARCEL_ERROR_TAG = "#_Song_Medium_parcel_error"

        fun toSong(songMedium: SongMedium): Song {
            val id = songMedium.id
            val name = songMedium.name
            if (name == PARCEL_ERROR_TAG) return Song.unknown
            val authors = mutableListOf<Author>()
            for (index in songMedium.authorIds.indices) {
                authors.add(
                    Author(
                        songMedium.authorIds[index],
                        songMedium.authorNames[index]
                    )
                )
            }
            if (songMedium.albumName == PARCEL_ERROR_TAG
                || songMedium.albumCoverUrl == PARCEL_ERROR_TAG
            ) return Song.unknown
            val album = Album(songMedium.albumId, songMedium.albumName, songMedium.albumCoverUrl)
            return Song(id, name, authors, album)
        }

        fun fromSong(song: Song): SongMedium {
            val id = song.id
            val name = song.name
            val authorIds = song.authors.map { it.id }
            val authorNames = song.authors.map { it.name }
            val albumId = song.album.id
            val albumName = song.album.name
            val albumCoverUrl = song.album.coverUrl
            return SongMedium(
                id, name,
                authorIds, authorNames,
                albumId, albumName, albumCoverUrl
            )
        }

        fun toSongs(songMediums: List<SongMedium>): List<Song> = songMediums.map { toSong(it) }

        fun fromSongs(songs: List<Song>): List<SongMedium> = songs.map { fromSong(it) }

        @JvmField
        val CREATOR = object : Parcelable.Creator<SongMedium> {
            override fun createFromParcel(parcel: Parcel): SongMedium {
                return SongMedium(parcel)
            }

            override fun newArray(size: Int): Array<SongMedium?> {
                return arrayOfNulls(size)
            }
        }
    }
}