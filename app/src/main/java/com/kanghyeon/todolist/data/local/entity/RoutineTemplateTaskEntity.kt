package com.kanghyeon.todolist.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 루틴 템플릿 하위 할 일 (TaskEntity의 청사진)
 *
 * - [groupId]: 소속 그룹 FK (그룹 삭제 시 CASCADE 삭제)
 * - priority: [Priority].value (Int) 와 동일한 형식으로 저장
 * - dueDate 없음 — 실제 TaskEntity 생성 시 오늘 날짜 기준으로 설정
 */
@Entity(
    tableName = "routine_template_tasks",
    foreignKeys = [
        ForeignKey(
            entity        = RoutineTemplateGroupEntity::class,
            parentColumns = ["id"],
            childColumns  = ["groupId"],
            onDelete      = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("groupId")],
)
data class RoutineTemplateTaskEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 소속 그룹 ID */
    val groupId: Long,

    val title: String,

    val description: String? = null,

    val priority: Int = Priority.MEDIUM.value,

    val showOnLockScreen: Boolean = true,
)
