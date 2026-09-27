package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MeepDao {
    @Query("SELECT * FROM meeps ORDER BY id ASC")
    fun getAllMeeps(): Flow<List<MeepEntity>>

    @Query("SELECT * FROM meeps WHERE isActive = 1 LIMIT 1")
    fun getActiveMeep(): Flow<MeepEntity?>

    @Query("SELECT * FROM meeps WHERE id = :id LIMIT 1")
    suspend fun getMeepById(id: Long): MeepEntity?

    @Query("SELECT COUNT(*) FROM meeps")
    fun getMeepCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeep(meep: MeepEntity): Long

    @Update
    suspend fun updateMeep(meep: MeepEntity)

    @Query("UPDATE meeps SET isActive = CASE WHEN id = :activeId THEN 1 ELSE 0 END")
    suspend fun setActiveMeep(activeId: Long)

    @Query("DELETE FROM meeps WHERE id = :id")
    suspend fun deleteMeep(id: Long)

    // User Account
    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(account: UserAccountEntity)

    @Query("UPDATE user_account SET meepPoints = meepPoints + :amount WHERE id = 1")
    suspend fun addPoints(amount: Int)

    @Query("UPDATE user_account SET meepPoints = meepPoints - :amount WHERE id = 1 AND meepPoints >= :amount")
    suspend fun spendPoints(amount: Int): Int

    // Owned Items
    @Query("SELECT * FROM owned_items")
    fun getOwnedItems(): Flow<List<OwnedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnedItem(item: OwnedItemEntity)

    // Park History
    @Query("SELECT * FROM park_history ORDER BY timestamp DESC LIMIT 20")
    fun getParkHistory(): Flow<List<ParkInteractionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkHistory(history: ParkInteractionHistoryEntity)
}
