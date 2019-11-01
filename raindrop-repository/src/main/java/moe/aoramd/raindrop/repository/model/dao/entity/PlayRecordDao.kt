package moe.aoramd.raindrop.repository.model.dao.entity

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.aoramd.raindrop.repository.entity.PlayRecord
import moe.aoramd.raindrop.repository.entity.SongMeta

@Dao
interface PlayRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playRecord: PlayRecord)

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