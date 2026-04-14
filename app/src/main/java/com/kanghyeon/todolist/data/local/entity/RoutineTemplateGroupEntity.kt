package com.kanghyeon.todolist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 루틴 템플릿 그룹 (예: "출근 루틴", "주말 루틴")
 *
 * 하나의 그룹이 여러 [RoutineTemplateTaskEntity]를 소유한다.
 * [isActive] = true인 그룹만 자정 자동 주입(generateDailyRoutines)에 참여한다.
 */
@Entity(tableName = "routine_template_groups")
data class RoutineTemplateGroupEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    /** true: 자동/수동 주입 허용, false: 비활성화 */
    val isActive: Boolean = true,

    val createdAt: Long = System.currentTimeMillis(),
)
