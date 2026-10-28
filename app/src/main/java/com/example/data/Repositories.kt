package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class PlatformRepository(private val platformDao: PlatformDao) {
    val allPlatforms: Flow<List<PlatformEntity>> = platformDao.getAllPlatforms()

    fun getPlatformsByCategory(category: String): Flow<List<PlatformEntity>> {
        return platformDao.getPlatformsByCategory(category)
    }

    suspend fun seedDatabaseIfEmpty() {
        val count = platformDao.getPlatformsCount()
        if (count == 0) {
            val seeds = AppDatabase.getSeedPlatforms()
            platformDao.insertPlatforms(seeds)
        }
    }
}

class RequestRepository(private val requestDao: RequestDao) {
    val allRequests: Flow<List<RequestEntity>> = requestDao.getAllRequests()

    suspend fun getRequestById(id: Int): RequestEntity? {
        return requestDao.getRequestById(id)
    }

    suspend fun insertRequest(request: RequestEntity): Long {
        return requestDao.insertRequest(request)
    }

    suspend fun updateRequest(request: RequestEntity) {
        requestDao.updateRequest(request)
    }

    suspend fun deleteRequestById(id: Int) {
        requestDao.deleteRequestById(id)
    }
}

class ChatMessageRepository(private val chatMessageDao: ChatMessageDao) {
    val chatHistory: Flow<List<ChatMessageEntity>> = chatMessageDao.getChatHistory()

    suspend fun insertMessage(message: ChatMessageEntity) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun clearHistory() {
        chatMessageDao.clearHistory()
    }
}
