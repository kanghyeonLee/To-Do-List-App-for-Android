package com.kanghyeon.todolist.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kanghyeon.todolist.R
import com.kanghyeon.todolist.data.local.entity.Priority
import com.kanghyeon.todolist.data.local.entity.TaskEntity
import com.kanghyeon.todolist.presentation.theme.CardBorderColor
import com.kanghyeon.todolist.presentation.theme.PriorityHigh
import com.kanghyeon.todolist.presentation.theme.PriorityLow
import com.kanghyeon.todolist.presentation.theme.PriorityMedium
import com.kanghyeon.todolist.presentation.viewmodel.TaskEvent
import com.kanghyeon.todolist.presentation.viewmodel.TaskUiState
import com.kanghyeon.todolist.presentation.viewmodel.TaskViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ══════════════════════════════════════════════════════════════════
// MainScreen — 루트 컴포저블
// ══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TaskViewModel = hiltViewModel(),
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val archiveDate  by viewModel.selectedArchiveDate.collectAsStateWithLifecycle()
    val archiveTasks by viewModel.archiveTasks.collectAsStateWithLifecycle()
    val editingTask  by viewModel.editingTask.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomSheet  by remember { mutableStateOf(false) }
    var showTrashScreen  by remember { mutableStateOf(false) }
    var selectedTab      by remember { mutableIntStateOf(0) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TaskEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message  = event.message,
                        duration = SnackbarDuration.Short,
                    )
                }
                is TaskEvent.TaskDeleted -> {
                    val result = snackbarHostState.showSnackbar(
                        message     = "휴지통으로 이동했습니다.",
                        actionLabel = "실행 취소",
                        duration    = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.restoreTask(event.task)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "To Do List",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF1D1D1F),
                        ),
                    )
                },
                actions = {
                    IconButton(onClick = { showTrashScreen = true }) {
                        Icon(
                            painter            = painterResource(R.drawable.trash_2),
                            contentDescription = "휴지통",
                            tint               = Color(0xFF6B7280),
                            modifier           = Modifier.size(22.dp),
                        )
                    }
                    if (selectedTab == 1 && archiveTasks.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearCompletedForSelectedDate) {
                            Icon(
                                painter            = painterResource(R.drawable.trash_2),
                                contentDescription = "이 날의 완료 항목 삭제",
                                tint               = MaterialTheme.colorScheme.error,
                                modifier           = Modifier.size(22.dp),
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor        = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor     = Color(0xFF1D1D1F),
                    actionIconContentColor = Color(0xFF6B7280),
                ),
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick        = { showBottomSheet = true },
                    shape          = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = Color.White,
                    elevation      = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp,
                        hoveredElevation = 10.dp,
                    ),
                ) {
                    Icon(
                        painter            = painterResource(R.drawable.plus),
                        contentDescription = "할 일 추가",
                        modifier           = Modifier.size(24.dp),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {

            // ── 탭: 할 일 / 아카이브 ─────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = MaterialTheme.colorScheme.surface,
                contentColor     = MaterialTheme.colorScheme.primary,
                indicator        = { tabPositions ->
                    // 2dp 얇은 라인 인디케이터 — 무거운 블록 제거
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(2.dp)
                            .padding(horizontal = 20.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
                            ),
                    )
                },
                divider = {
                    HorizontalDivider(
                        color     = CardBorderColor,
                        thickness = 1.dp,
                    )
                },
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = {
                        val count = uiState.activeTasks.size
                        Text(
                            text  = if (count > 0) "할 일 ($count)" else "할 일",
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.primary
                                    else Color(0xFF6B7280),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedTab == 0) FontWeight.SemiBold
                                             else FontWeight.Normal,
                            ),
                        )
                    },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = {
                        val count = uiState.completedTasks.size
                        Text(
                            text  = if (count > 0) "아카이브 ($count)" else "아카이브",
                            color = if (selectedTab == 1) MaterialTheme.colorScheme.primary
                                    else Color(0xFF6B7280),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedTab == 1) FontWeight.SemiBold
                                             else FontWeight.Normal,
                            ),
                        )
                    },
                )
            }

            // ── 탭 콘텐츠 ────────────────────────────────────
            when {
                uiState.isLoading -> LoadingContent()
                selectedTab == 0  -> TodoContent(uiState, viewModel, onEdit = { viewModel.setEditingTask(it) })
                else              -> ArchiveContent(archiveDate, archiveTasks, viewModel)
            }
        }
    }

    if (showTrashScreen) {
        TrashScreen(
            viewModel = viewModel,
            onBack    = { showTrashScreen = false },
        )
    }

    if (showBottomSheet || editingTask != null) {
        AddTaskBottomSheet(
            task      = editingTask,
            onDismiss = {
                showBottomSheet = false
                viewModel.setEditingTask(null)
            },
            onSave = { title, desc, priority, dueDate, showOnLock, reminderMinutes ->
                viewModel.saveCurrentTask(
                    title            = title,
                    description      = desc,
                    priority         = priority,
                    dueDate          = dueDate,
                    showOnLockScreen = showOnLock,
                    reminderMinutes  = reminderMinutes,
                )
                showBottomSheet = false
            },
        )
    }
}

// ══════════════════════════════════════════════════════════════════
// 탭 콘텐츠: 할 일 (미완료 priority 섹션)
// ══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TodoContent(
    uiState: TaskUiState,
    viewModel: TaskViewModel,
    onEdit: (TaskEntity) -> Unit,
) {
    if (uiState.activeTasks.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenSectionHeader(
                title   = "오늘의 할 일",
                iconRes = R.drawable.house,
            )
            EmptyContent(
                iconRes    = R.drawable.house,
                message    = "현재 등록된 할 일이 없습니다",
                subMessage = "아래 + 버튼을 눌러 첫 번째 할 일을 추가해 보세요.",
            )
        }
        return
    }

    val grouped = uiState.activeTasks.groupBy { Priority.from(it.priority) }

    data class PriorityMeta(val label: String, val accent: Color)
    val priorityMeta = mapOf(
        Priority.HIGH   to PriorityMeta("높음", PriorityHigh),
        Priority.MEDIUM to PriorityMeta("중간", PriorityMedium),
        Priority.LOW    to PriorityMeta("낮음", PriorityLow),
    )

    LazyColumn(
        modifier        = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding  = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 섹션 타이틀 헤더
        item {
            ScreenSectionHeader(
                title   = "오늘의 할 일",
                iconRes = R.drawable.house,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
        }

        Priority.entries
            .sortedByDescending { it.value }
            .forEach { priority ->
                val tasks = grouped[priority]
                if (!tasks.isNullOrEmpty()) {
                    val meta = priorityMeta.getValue(priority)

                    stickyHeader(key = "priority_header_${priority.name}") {
                        PrioritySectionHeader(
                            label    = meta.label,
                            count    = tasks.size,
                            dotColor = meta.accent,
                        )
                    }

                    item(key = "priority_card_${priority.name}") {
                        PriorityGroupCard(
                            tasks        = tasks,
                            accentColor  = meta.accent,
                            bgColor      = MaterialTheme.colorScheme.surface,
                            onToggleDone = { task -> viewModel.toggleTaskCompletion(task) },
                            onDelete     = { task -> viewModel.deleteTask(task) },
                            onEdit       = { task -> onEdit(task) },
                            modifier     = Modifier.animateItem(),
                        )
                    }
                }
            }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ══════════════════════════════════════════════════════════════════
// 탭 콘텐츠: 아카이브 (날짜별 완료 항목)
// ══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchiveContent(
    archiveDate: Long,
    archiveTasks: List<TaskEntity>,
    viewModel: TaskViewModel,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = archiveDate,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis <= System.currentTimeMillis() + 86_400_000L
            },
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.selectArchiveDate(it) }
                    showDatePicker = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val selectedLocalDate = remember(archiveDate) {
        Instant.ofEpochMilli(archiveDate).atZone(ZoneId.systemDefault()).toLocalDate()
    }
    val dateLabel = remember(selectedLocalDate) {
        selectedLocalDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREA))
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── 아카이브 섹션 타이틀 ─────────────────────────────
        ScreenSectionHeader(
            title   = "아카이브",
            iconRes = R.drawable.calendar_check,
            modifier = Modifier.padding(top = 8.dp),
        )

        // ── 날짜 선택 버튼 — 미니멀 카드 스타일 ──────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp))
                .clickable { showDatePicker = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter            = painterResource(R.drawable.calendar),
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text  = dateLabel,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1D1D1F),
                ),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text  = "▼",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF6B7280),
                ),
            )
        }

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

        // ── 해당 날짜 완료 목록 ───────────────────────────────
        if (archiveTasks.isEmpty()) {
            EmptyContent(
                iconRes    = R.drawable.calendar_check,
                message    = "현재 등록된 할 일이 없습니다",
                subMessage = "이 날짜에 완료된 할 일이 없어요.\n할 일을 체크하면 날짜별로 기록됩니다.",
            )
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = archiveTasks,
                    key   = { "archive_${it.id}" },
                ) { task ->
                    TaskItem(
                        task         = task,
                        onToggleDone = { viewModel.toggleTaskCompletion(task) },
                        onDelete     = { viewModel.deleteTask(task) },
                        modifier     = Modifier.animateItem(),
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 공통 서브 컴포저블
// ══════════════════════════════════════════════════════════════════

/**
 * 탭 콘텐츠 최상단에 배치하는 큰 타이틀 헤더.
 * 아이콘 + 굵은 타이틀 텍스트 구성.
 */
@Composable
private fun ScreenSectionHeader(
    title: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(10.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter            = painterResource(iconRes),
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(18.dp),
            )
        }
        Text(
            text  = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1D1D1F),
            ),
        )
    }
}

/**
 * 중요도 섹션용 sticky 헤더.
 * 스크롤 시 상단에 고정되므로 [MaterialTheme.colorScheme.background] 배경으로 가림막 처리.
 */
@Composable
private fun PrioritySectionHeader(
    label: String,
    count: Int,
    dotColor: Color,
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color      = dotColor,
            ),
        )
        Text(
            text  = count.toString(),
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color(0xFF6B7280),
            ),
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

/**
 * 리스트가 비었을 때 표시되는 Empty State 컴포저블.
 * 아이콘을 연한 원형 배경 위에 올려 시각적 완성도를 높임.
 */
@Composable
private fun EmptyContent(
    iconRes: Int,
    message: String,
    subMessage: String,
) {
    AnimatedVisibility(
        visible = true,
        enter   = fadeIn(),
        exit    = fadeOut(),
    ) {
        Box(
            modifier         = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // 아이콘을 감싸는 연한 원형 배경
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter            = painterResource(iconRes),
                        contentDescription = null,
                        modifier           = Modifier.size(40.dp),
                        tint               = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text  = message,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF1D1D1F),
                    ),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text      = subMessage,
                    style     = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF6B7280),
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
