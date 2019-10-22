package moe.aoramd.raindrop.repository.model.dao.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.aoramd.raindrop.repository.entity.Playlist

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist WHERE id = :id LIMIT 1")
    suspend fun query(id: Long): Playlist?

    @Query("SELECT * FROM playlist WHERE id IN (:ids) ORDER BY title")
    suspend fun queryAll(ids: List<Long>): List<Playlist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlists: List<Playlist>)
}