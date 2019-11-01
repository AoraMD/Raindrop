package moe.aoramd.raindrop.repository.model.dao.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.aoramd.raindrop.repository.entity.Album

@Dao
interface AlbumDao {
    @Query("SELECT * FROM album WHERE id IN (:ids)")
    suspend fun queryAll(ids: List<Long>): List<Album>

    @Query("SELECT * FROM album WHERE id = :id LIMIT 1")
    suspend fun query(id: Long): Album?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(albums: List<Album>)
}