package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatformDao {
    @Query("SELECT * FROM platforms ORDER BY nameArabic ASC")
    fun getAllPlatforms(): Flow<List<PlatformEntity>>

    @Query("SELECT * FROM platforms WHERE categoryArabic = :category ORDER BY nameArabic ASC")
    fun getPlatformsByCategory(category: String): Flow<List<PlatformEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatform(platform: PlatformEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatforms(platforms: List<PlatformEntity>)

    @Query("SELECT COUNT(*) FROM platforms")
    suspend fun getPlatformsCount(): Int
}

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<RequestEntity>>

    @Query("SELECT * FROM requests WHERE id = :requestId")
    suspend fun getRequestById(requestId: Int): RequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: RequestEntity): Long

    @Update
    suspend fun updateRequest(request: RequestEntity)

    @Query("DELETE FROM requests WHERE id = :requestId")
    suspend fun deleteRequestById(requestId: Int)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatHistory(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}
