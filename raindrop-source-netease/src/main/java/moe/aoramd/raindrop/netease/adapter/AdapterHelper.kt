package moe.aoramd.raindrop.netease.adapter

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import moe.aoramd.raindrop.repository.Tags

object AdapterHelper {
    internal fun JsonReader.nextStringNullable(): String {
        if (peek() == JsonToken.NULL) {
            nextNull()
            return Tags.OFFLINE_TAG
        }
        return nextString()
    }
}