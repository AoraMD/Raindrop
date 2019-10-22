package moe.aoramd.raindrop.repository.model.dao.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import moe.aoramd.raindrop.repository.entity.Account

@Dao
interface AccountDao {
    @Query("SELECT * FROM account WHERE id = :id LIMIT 1")
    suspend fun query(id: Long): Account?

    @Insert
    suspend fun insert(account: Account)
}