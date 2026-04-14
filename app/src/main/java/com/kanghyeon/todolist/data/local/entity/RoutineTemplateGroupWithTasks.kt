package com.kanghyeon.todolist.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Room @Transaction 쿼리 결과: 그룹 + 소속 할 일 목록 (1:N 관계)
 *
 * @Embedded: RoutineTemplateGroupEntity 컬럼을 평탄하게 포함
 * @Relation: groupId를 기준으로 tasks 리스트를 즉시 로딩
 */
data class RoutineTemplateGroupWithTasks(

    @Embedded
    val group: RoutineTemplateGroupEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "groupId",
    )
    val tasks: List<RoutineTemplateTaskEntity>,
)
