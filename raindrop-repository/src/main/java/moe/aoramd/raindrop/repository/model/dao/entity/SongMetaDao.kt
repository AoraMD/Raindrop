package moe.aoramd.raindrop.repository.model.dao.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.aoramd.raindrop.repository.entity.SongMeta

@Dao
interface SongMetaDao {
    @Query("SELECT * FROM song WHERE id = :id LIMIT 1")
    suspend fun query(id: Long): SongMeta?

    @Query("SELECT * FROM song WHERE id IN (:ids)")
    suspend fun queryAll(ids: List<Long>): List<SongMeta>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songMeta: SongMeta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songMetas: List<SongMeta>)
}