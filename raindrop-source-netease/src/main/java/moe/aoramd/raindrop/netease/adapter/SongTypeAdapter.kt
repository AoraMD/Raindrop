package moe.aoramd.raindrop.netease.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import moe.aoramd.raindrop.netease.adapter.AdapterHelper.nextStringNullable
import moe.aoramd.raindrop.repository.Tags
import moe.aoramd.raindrop.repository.entity.Album
import moe.aoramd.raindrop.repository.entity.Author
import moe.aoramd.raindrop.repository.entity.Song

class SongTypeAdapter : TypeAdapter<Song>() {
    override fun write(out: JsonWriter?, value: Song?) {
        out?.apply {
            beginObject()
            name("id").value(value?.id)
            name("name").value(value?.name)

            name("ar").beginArray()
            value?.authors?.let {
                for (author in it) {
                    beginObject()
                    name("id").value(author.id)
                    name("name").value(author.name)
                    endObject()
                }
            }
            endArray()

            name("al").beginObject()
            name("id").value(value?.album?.id)
            name("name").value(value?.album?.name)
            name("picUrl").value(value?.album?.coverUrl)
            endObject()

            endObject()
        }
    }

    override fun read(`in`: JsonReader?): Song {
        var id = Tags.OFFLINE_ID
        var name = Tags.OFFLINE_TAG
        val authors = mutableListOf<Author>()
        var album: Album = Album.offline
        `in`?.apply {
            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "id" -> id = nextLong()
                    "name" -> name = nextStringNullable()
                    "ar", "artists" -> {
                        beginArray()
                        while (hasNext()) {
                            var authorId = Tags.OFFLINE_ID
                            var authorName = Tags.OFFLINE_TAG
                            beginObject()
                            while (hasNext()) {
                                when (nextName()) {
                                    "id" -> authorId = nextLong()
                                    "name" -> authorName = nextStringNullable()
                                    else -> skipValue()
                                }
                            }
                            endObject()
                            authors.add(Author(authorId, authorName))
                        }
                        endArray()
                    }
                    "al", "album" -> {
                        var albumId = Tags.OFFLINE_ID
                        var albumName = Tags.OFFLINE_TAG
                        var albumCoverUrl = Tags.OFFLINE_TAG
                        beginObject()
                        while (hasNext()) {
                            when (nextName()) {
                                "id" -> albumId = nextLong()
                                "name" -> albumName = nextStringNullable()
                                "picUrl" -> albumCoverUrl = nextStringNullable()
                                else -> skipValue()
                            }
                        }
                        endObject()
                        album = Album(albumId, albumName, albumCoverUrl)
                    }
                    else -> skipValue()
                }
            }
            endObject()
        }
        return Song(id, name, authors, album)
    }
}

