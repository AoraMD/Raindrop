package moe.aoramd.raindrop.netease.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import moe.aoramd.raindrop.netease.adapter.AdapterHelper.nextStringNullable
import moe.aoramd.raindrop.repository.entity.Account

class AccountTypeAdapter : TypeAdapter<Account>() {
    override fun write(out: JsonWriter?, value: Account?) {
        out?.apply {
            beginObject()
            name("userId").value(value?.id)
            name("nickname").value(value?.nickname)
            name("signature").value(value?.signature)
            name("avatarUrl").value(value?.avatarUrl)
            name("backgroundUrl").value(value?.backgroundUrl)
            endObject()
        }
    }

    override fun read(`in`: JsonReader?): Account {
        var id = Account.offline.id
        var nickname = Account.offline.nickname
        var signature = Account.offline.signature
        var avatarUrl = Account.offline.avatarUrl
        var backgroundUrl = Account.offline.backgroundUrl
        `in`?.apply {
            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "userId" -> id = nextLong()
                    "nickname" -> nickname = nextStringNullable()
                    "signature" -> signature = nextStringNullable()
                    "avatarUrl" -> avatarUrl = nextStringNullable()
                    "backgroundUrl" -> backgroundUrl = nextStringNullable()
                    else -> skipValue()
                }
            }
            endObject()
        }
        return Account(id, nickname, signature, avatarUrl, backgroundUrl)
    }

}