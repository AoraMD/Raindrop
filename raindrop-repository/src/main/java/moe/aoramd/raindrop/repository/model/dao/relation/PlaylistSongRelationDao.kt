package moe.aoramd.raindrop.repository.model.dao.relation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.aoramd.raindrop.repository.entity.SongMeta
import moe.aoramd.raindrop.repository.model.relation.PlaylistSongRelation

@Dao
interface PlaylistSongRelationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(relations: List<PlaylistSongRelation>)

    @Query(
        """
        SELECT * FROM song
        INNER JOIN playlist_song_relation
        ON song.id = playlist_song_relation.song
        WHERE playlist_song_relation.playlist = :playlistId
    """
    )
    suspend fun query(playlistId: Long): List<SongMeta>
}