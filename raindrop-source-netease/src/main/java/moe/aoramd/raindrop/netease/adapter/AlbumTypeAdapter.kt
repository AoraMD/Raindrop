package moe.aoramd.raindrop.netease.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import moe.aoramd.raindrop.netease.adapter.AdapterHelper.nextStringNullable
import moe.aoramd.raindrop.repository.Tags
import moe.aoramd.raindrop.repository.entity.Album

class AlbumTypeAdapter : TypeAdapter<Album>() {
    override fun write(out: JsonWriter?, value: Album?) {
        out?.apply {
            beginObject()
            name("id").value(value?.id)
            name("name").value(value?.name)
            name("picUrl").value(value?.coverUrl)
            endObject()
        }
    }

    override fun read(`in`: JsonReader?): Album {
        var id = Tags.UNKNOWN_ID
        var name = Tags.UNKNOWN_TAG
        var coverUrl = Tags.UNKNOWN_TAG
        `in`?.apply {
            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "id" -> id = nextLong()
                    "name" -> name = nextStringNullable()
                    "picUrl" -> coverUrl = nextStringNullable()
                    else -> skipValue()
                }
            }
            endObject()
        }
        return Album(id, name, coverUrl)
    }

}