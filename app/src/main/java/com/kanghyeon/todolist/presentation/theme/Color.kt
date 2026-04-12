package com.kanghyeon.todolist.presentation.theme

import androidx.compose.ui.graphics.Color

// ── 앱 시드 색상 (초록 계열) ──────────────────────────────────────
val TodoGreen        = Color(0xFF2E7D32)
val TodoGreenLight   = Color(0xFF60AD5E)
val TodoGreenDark    = Color(0xFF005005)
val TodoGreenContainer  = Color(0xFFC8E6C9)
val OnTodoGreenContainer = Color(0xFF00210A)

// ── 서피스 / 배경 ─────────────────────────────────────────────────
val TodoSurface      = Color(0xFFFAFAFA)
val TodoBackground   = Color(0xFFF5F5F5)

// ── 우선순위 색상 (Compose에서 사용) ──────────────────────────────
val PriorityHigh     = Color(0xFFE53935)   // Red 600
val PriorityMedium   = Color(0xFFFB8C00)   // Orange 600 (파란색에서 주황색으로 통일)
val PriorityLow      = Color(0xFF9E9E9E)   // Grey 400

// ── 우선순위 파스텔 배경 (카드 섹션 배경에 사용) ──────────────────
val PriorityHighContainer    = Color(0x1AE53935)   // Red α≈10%
val PriorityMediumContainer  = Color(0x1AFB8C00)   // Orange α≈10%
val PriorityLowContainer     = Color(0x0F9E9E9E)   // Grey α≈6%

// ── 우선순위 강조 텍스트 (가독성 확보용 진한 색) ─────────────────
val PriorityHighDark    = Color(0xFFC62828)   // Red 800
val PriorityMediumDark  = Color(0xFFE65100)   // Orange 900
val PriorityLowDark     = Color(0xFF424242)   // Grey 800

// ── 기한 초과 ─────────────────────────────────────────────────────
val OverdueRed       = Color(0xFFFF5252)
val OverdueRedContainer = Color(0xFFFFEBEE)

// ── 삭제 스와이프 배경 ────────────────────────────────────────────
val SwipeDeleteBackground = Color(0xFFE53935)
