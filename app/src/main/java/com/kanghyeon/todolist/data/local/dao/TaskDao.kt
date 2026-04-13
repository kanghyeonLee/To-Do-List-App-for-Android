package com.kanghyeon.todolist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.kanghyeon.todolist.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // ──────────────────────────────────────────
    // READ
    // ──────────────────────────────────────────

    @Query(
        """
        SELECT * FROM tasks
        WHERE showOnLockScreen = 1 AND isDone = 0 AND isDeleted = 0
        ORDER BY priority DESC, dueDate ASC, sortOrder ASC
        """
    )
    fun getLockScreenTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id AND isDeleted = 0")
    fun getTaskById(id: Long): Flow<TaskEntity?>

    @Query(
        """
        SELECT * FROM tasks
        WHERE isDone = 1 AND isDeleted = 0
        ORDER BY updatedAt DESC
        """
    )
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE isDone = 1 AND isDeleted = 0
          AND updatedAt >= :startOfDay
          AND updatedAt <= :endOfDay
        ORDER BY updatedAt DESC
        """
    )
    fun getCompletedTasksByDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query(
        """
        DELETE FROM tasks
        WHERE isDone = 1 AND isDeleted = 0
          AND updatedAt >= :startOfDay
          AND updatedAt <= :endOfDay
        """
    )
    suspend fun deleteCompletedByDateRange(startOfDay: Long, endOfDay: Long)

    @Query(
        """
        SELECT * FROM tasks
        WHERE isDone = 0 AND isDeleted = 0
        ORDER BY priority DESC, createdAt DESC
        """
    )
    fun getActiveTasks(): Flow<List<TaskEntity>>

    /** 휴지통 목록 — isDeleted = 1인 항목, 최신 삭제 순 */
    @Query(
        """
        SELECT * FROM tasks
        WHERE isDeleted = 1
        ORDER BY updatedAt DESC
        """
    )
    fun getDeletedTasks(): Flow<List<TaskEntity>>

    // ──────────────────────────────────────────
    // WRITE
    // ──────────────────────────────────────────

    @Upsert
    suspend fun upsert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query(
        """
        UPDATE tasks
        SET isDone = :isDone,
            updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateDoneStatus(
        id: Long,
        isDone: Boolean,
        updatedAt: Long = System.currentTimeMillis(),
    )

    /** Soft Delete: 휴지통으로 이동 */
    @Query(
        """
        UPDATE tasks
        SET isDeleted = 1,
            updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun softDelete(id: Long, updatedAt: Long = System.currentTimeMillis())

    /** 휴지통 복구: isDeleted → 0 */
    @Query(
        """
        UPDATE tasks
        SET isDeleted = 0,
            updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun restoreFromTrash(id: Long, updatedAt: Long = System.currentTimeMillis())

    /** 휴지통 비우기: isDeleted = 1인 항목 전부 영구 삭제 */
    @Query("DELETE FROM tasks WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Query("UPDATE tasks SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: Long, sortOrder: Int)

    /** 영구 삭제 (단건) */
    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM tasks WHERE isDone = 1 AND isDeleted = 0")
    suspend fun deleteAllCompleted()
}
