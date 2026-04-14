package com.kanghyeon.todolist.data.repository

import com.kanghyeon.todolist.data.local.entity.RoutineTemplateGroupWithTasks
import com.kanghyeon.todolist.data.local.entity.RoutineTemplateTaskEntity
import kotlinx.coroutines.flow.Flow

interface RoutineTemplateRepository {

    /** 전체 그룹 + 할 일 실시간 스트림 (UI 구독용) */
    fun getAllGroupsWithTasks(): Flow<List<RoutineTemplateGroupWithTasks>>

    /** 활성 그룹 단건 조회 (자정 자동 주입용) */
    suspend fun getActiveGroupsWithTasksOnce(): List<RoutineTemplateGroupWithTasks>

    /** 특정 그룹 단건 조회 (즉시 수동 주입용) */
    suspend fun getGroupWithTasksOnce(groupId: Long): RoutineTemplateGroupWithTasks?

    suspend fun addGroup(name: String): Long
    suspend fun updateGroupName(id: Long, name: String)
    suspend fun updateGroupActiveState(id: Long, isActive: Boolean)
    suspend fun deleteGroup(id: Long)

    suspend fun addTask(task: RoutineTemplateTaskEntity): Long
    suspend fun deleteTask(id: Long)
}
