package moe.aoramd.raindrop.netease.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import moe.aoramd.raindrop.netease.adapter.AdapterHelper.nextStringNullable
import moe.aoramd.raindrop.repository.Tags
import moe.aoramd.raindrop.repository.entity.Playlist

class PlaylistTypeAdapter : TypeAdapter<Playlist>() {

    override fun write(out: JsonWriter?, value: Playlist?) {
        out?.apply {
            beginObject()
            name("id").value(value?.id)
            name("userId").value(value?.accountId)
            name("name").value(value?.title)
            name("coverImgUrl").value(value?.coverUrl)
            name("trackCount").value(value?.trackCount)
            endObject()
        }
    }

    override fun read(`in`: JsonReader?): Playlist {
        var id = Tags.UNKNOWN_ID
        var accountId = Tags.UNKNOWN_ID
        var title = Tags.UNKNOWN_TAG
        var coverUrl = Tags.UNKNOWN_TAG
        var trackCount = 0
        `in`?.apply {
            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "id" -> id = nextLong()
                    "userId" -> accountId = nextLong()
                    "name" -> title = nextStringNullable()
                    "coverImgUrl" -> coverUrl = nextStringNullable()
                    "trackCount" -> trackCount = nextInt()
                    else -> skipValue()
                }
            }
            endObject()
        }
        return Playlist(id, accountId, title, coverUrl, trackCount)
    }

}