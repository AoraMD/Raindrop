package moe.aoramd.raindrop.repository.model.dao.relation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.model.relation.AccountPlaylistRelation

@Dao
interface AccountPlaylistRelationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(relations: List<AccountPlaylistRelation>)

    @Query(
        """
        SELECT * FROM playlist
        INNER JOIN account_playlist_relation
        ON playlist.id = account_playlist_relation.playlist
        WHERE account_playlist_relation.account = :accountId
    """
    )
    suspend fun query(accountId: Long): List<Playlist>
}