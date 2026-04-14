package com.kanghyeon.todolist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kanghyeon.todolist.data.local.entity.RoutineTemplateGroupEntity
import com.kanghyeon.todolist.data.local.entity.RoutineTemplateGroupWithTasks
import com.kanghyeon.todolist.data.local.entity.RoutineTemplateTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineTemplateDao {

    // ── READ ─────────────────────────────────────────────────────────

    /** 전체 그룹 + 하위 할 일 목록 (생성 순) — UI 실시간 구독용 Flow */
    @Transaction
    @Query("SELECT * FROM routine_template_groups ORDER BY createdAt ASC")
    fun getAllGroupsWithTasks(): Flow<List<RoutineTemplateGroupWithTasks>>

    /** 활성 그룹만 단건 조회 (자정 자동 주입 전용 suspend) */
    @Transaction
    @Query("SELECT * FROM routine_template_groups WHERE isActive = 1")
    suspend fun getActiveGroupsWithTasksOnce(): List<RoutineTemplateGroupWithTasks>

    /** 특정 그룹 단건 조회 (즉시 주입 전용 suspend) */
    @Transaction
    @Query("SELECT * FROM routine_template_groups WHERE id = :groupId LIMIT 1")
    suspend fun getGroupWithTasksOnce(groupId: Long): RoutineTemplateGroupWithTasks?

    // ── GROUP WRITE ───────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: RoutineTemplateGroupEntity): Long

    @Query("UPDATE routine_template_groups SET name = :name WHERE id = :id")
    suspend fun updateGroupName(id: Long, name: String)

    @Query("UPDATE routine_template_groups SET isActive = :isActive WHERE id = :id")
    suspend fun updateGroupActiveState(id: Long, isActive: Boolean)

    @Query("DELETE FROM routine_template_groups WHERE id = :id")
    suspend fun deleteGroup(id: Long)

    // ── TASK WRITE ────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: RoutineTemplateTaskEntity): Long

    @Query("DELETE FROM routine_template_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}
