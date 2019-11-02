package moe.aoramd.raindrop.repository.model.dao.entity

import androidx.paging.DataSource
import androidx.room.*
import moe.aoramd.raindrop.repository.entity.PlayRecord
import moe.aoramd.raindrop.repository.entity.SongMeta

@Dao
interface PlayRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playRecord: PlayRecord)

    @Query("DELETE FROM play_record WHERE song = :songId")
    suspend fun delete(songId: Long)

    @Query("DELETE FROM play_record")
    suspend fun deleteAll()

    @Query(
        """
        SELECT * FROM song
        INNER JOIN play_record
        ON song.id = play_record.song
        ORDER BY play_record.playTime DESC
    """
    )
    fun queryAll(): DataSource.Factory<Int, SongMeta>
}