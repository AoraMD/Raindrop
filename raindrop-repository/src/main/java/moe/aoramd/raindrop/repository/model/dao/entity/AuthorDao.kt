package moe.aoramd.raindrop.repository.model.dao.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.aoramd.raindrop.repository.entity.Author

@Dao
interface AuthorDao {
    @Query("SELECT * FROM author WHERE id IN (:ids)")
    suspend fun queryAll(ids: List<Long>): List<Author>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(authors: List<Author>)
}