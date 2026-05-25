package com.example.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "chat_history")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_history ORDER BY timestamp ASC")
    fun getChatFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_history")
    suspend fun clearHistory()
}

@Entity(tableName = "digital_assets")
data class DigitalAsset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "image", "code", "audio", "video", "report"
    val prompt: String,
    val resultUrlOrBase64: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface DigitalAssetDao {
    @Query("SELECT * FROM digital_assets ORDER BY createdAt DESC")
    fun getAllAssetsFlow(): Flow<List<DigitalAsset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: DigitalAsset)

    @Query("DELETE FROM digital_assets WHERE id = :id")
    suspend fun deleteAsset(id: Int)
}

@Entity(tableName = "agent_tasks")
data class AgentTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val category: String, // "Research", "Coding", "Automation", "Marketing"
    val status: String, // "Pending", "Running", "Completed", "Failed"
    val outputLog: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Dao
interface AgentTaskDao {
    @Query("SELECT * FROM agent_tasks ORDER BY lastUpdated DESC")
    fun getAllTasksFlow(): Flow<List<AgentTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AgentTask)

    @Query("UPDATE agent_tasks SET status = :status, outputLog = :outputLog, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateTaskStatus(id: Int, status: String, outputLog: String, lastUpdated: Long = System.currentTimeMillis())

    @Query("DELETE FROM agent_tasks")
    suspend fun clearAllTasks()
}

@Database(entities = [ChatMessage::class, DigitalAsset::class, AgentTask::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun digitalAssetDao(): DigitalAssetDao
    abstract fun agentTaskDao(): AgentTaskDao
}
