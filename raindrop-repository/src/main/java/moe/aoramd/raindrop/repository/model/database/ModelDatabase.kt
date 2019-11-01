package moe.aoramd.raindrop.repository.model.database

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import moe.aoramd.raindrop.repository.entity.*
import moe.aoramd.raindrop.repository.model.dao.entity.*
import moe.aoramd.raindrop.repository.model.dao.relation.AccountPlaylistRelationDao
import moe.aoramd.raindrop.repository.model.dao.relation.PlaylistSongRelationDao
import moe.aoramd.raindrop.repository.model.relation.AccountPlaylistRelation
import moe.aoramd.raindrop.repository.model.relation.PlaylistSongRelation
import moe.aoramd.lookinglass.manager.ContextManager

@Database(
    entities = [
        Account::class,
        Playlist::class,
        Album::class,
        Author::class,
        PlayRecord::class,
        SongMeta::class,
        AccountPlaylistRelation::class,
        PlaylistSongRelation::class
    ],
    version = 1
)
@TypeConverters(ModelDatabase.IdListConverter::class)
abstract class ModelDatabase : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME = "raindrop"

        @JvmStatic
        val instance: ModelDatabase by lazy {
            Room.databaseBuilder(
                ContextManager.context,
                ModelDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }

    // entity
    abstract fun accountDao(): AccountDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun albumDao(): AlbumDao

    abstract fun authorDao(): AuthorDao

    abstract fun playRecordDao(): PlayRecordDao

    abstract fun songMetaDao(): SongMetaDao

    // relation
    abstract fun accountPlaylistRelationDao(): AccountPlaylistRelationDao

    abstract fun playlistSongRelationDao(): PlaylistSongRelationDao

    internal class IdListConverter {
        @TypeConverter
        fun toIds(value: String): List<Long> =
            Gson().fromJson(value, object : TypeToken<List<Long>>() {}.type)

        @TypeConverter
        fun fromIds(ids: List<Long>): String = Gson().toJson(ids)
    }
}