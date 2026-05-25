package com.example.db

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class LilbedRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "lilbed_ai_database"
    ).fallbackToDestructiveMigration().build()

    private val chatDao = db.chatDao()
    private val digitalAssetDao = db.digitalAssetDao()
    private val agentTaskDao = db.agentTaskDao()

    // Chat operations
    val chatFlow: Flow<List<ChatMessage>> = chatDao.getChatFlow()

    suspend fun saveChatMessage(role: String, content: String) {
        chatDao.insertMessage(ChatMessage(role = role, content = content))
    }

    suspend fun clearChat() {
        chatDao.clearHistory()
    }

    // Generated asset operations
    val assetFlow: Flow<List<DigitalAsset>> = digitalAssetDao.getAllAssetsFlow()

    suspend fun saveAsset(type: String, prompt: String, content: String) {
        digitalAssetDao.insertAsset(DigitalAsset(type = type, prompt = prompt, resultUrlOrBase64 = content))
    }

    suspend fun removeAsset(id: Int) {
        digitalAssetDao.deleteAsset(id)
    }

    // Agent operations
    val tasksFlow: Flow<List<AgentTask>> = agentTaskDao.getAllTasksFlow()

    suspend fun addAgentTask(name: String, description: String, category: String, status: String = "Pending") {
        agentTaskDao.insertTask(AgentTask(name = name, description = description, category = category, status = status))
    }

    suspend fun updateAgentTask(id: Int, status: String, output: String) {
        agentTaskDao.updateTaskStatus(id, status, output)
    }

    suspend fun clearTasks() {
        agentTaskDao.clearAllTasks()
    }
}
