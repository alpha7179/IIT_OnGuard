// app/src/main/java/com/onguard/presentation/ui/dashboard/DashboardComponents.kt

package com.onguard.presentation.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onguard.presentation.theme.*

// ==== 1. 공통 카드 스타일 ====
@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardBackground,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = content
    )
}

// ==== 2. 상단 작은 통계 카드 (글래스모피즘) ====
@Composable
fun SmallStatCard(
    title: String,
    count: Int,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(110.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.3f),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = iconColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 28.sp
                )
                Text(
                    text = "회",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ==== 3. 중간 차트 카드 (글래스모피즘) ====
@Composable
fun ChartStatCard(
    title: String,
    count: String,
    iconColor: Color,
    unit: String,
    chartPlaceholder: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(130.dp) // 110dp -> 130dp로 증가
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.5f),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp) // 패딩 조정
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Analytics,
                        null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = iconColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = count,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Box(modifier = Modifier.size(40.dp)) {
                    chartPlaceholder()
                }
            }
        }
    }
}

// 미니 바 차트 플레이스홀더
@Composable
fun MiniBarChart(color: Color) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f)
        heights.forEach { height ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(height)
                    .background(color, shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
            )
        }
    }
}

// ==== 4. 탭 바 컴포넌트 ====
@Composable
fun DashboardTabBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    // 탭바 전체를 감싸는 알약 모양의 글래스모피즘 박스
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp) // 좌우 여백을 주어 플로팅 느낌
            .height(64.dp) // 높이 약간 증가
    ) {
        // 1. 배경 그림자 (은은한 깊이감)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(CircleShape) // 완전한 원형(알약)
                .background(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 300f
                    )
                )
        )

        // 2. 메인 글래스 패널 (알약 모양)
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape, // Capsule Shape
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp, // 테두리 두께 약간 증가
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.6f), // 상단 왼쪽: 강한 빛 반사
                        Color.White.copy(alpha = 0.1f), // 중간: 투명
                        Color.White.copy(alpha = 0.05f),
                        Color.White.copy(alpha = 0.3f)  // 하단 오른쪽: 은은한 반사
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
        ) {
            // 내부 반투명 배경 (그라데이션)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(6.dp) // 내부 패딩
            ) {
                // *** 3. 슬라이딩 인디케이터 및 탭 아이템 구현 ***
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val tabWidth = maxWidth / DashboardTab.values().size
                    
                    // 인디케이터 위치 애니메이션
                    val indicatorOffset by animateDpAsState(
                        targetValue = tabWidth * selectedTab.ordinal,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy
                        ),
                        label = "indicatorOffset"
                    )

                    // 3-1. 움직이는 인디케이터 (선택된 탭 배경)
                    Surface(
                        modifier = Modifier
                            .width(tabWidth)
                            .fillMaxHeight()
                            .offset(x = indicatorOffset),
                        color = Color.White.copy(alpha = 0.85f), // 불투명에 가까운 흰색
                        shape = CircleShape, // 둥근 알약 모양
                        shadowElevation = 4.dp // 그림자 추가로 입체감
                    ) {}

                    // 3-2. 탭 아이템들 (Row로 배치)
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        DashboardTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            
                            // 텍스트/아이콘 색상 애니메이션
                            val contentColor by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFF1A1A1A) else TextSecondary.copy(alpha = 0.9f),
                                animationSpec = tween(durationMillis = 300),
                                label = "contentColor"
                            )

                            Box(
                                modifier = Modifier
                                    .width(tabWidth)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .clickable { onTabSelected(tab) },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        tab.icon,
                                        null,
                                        tint = contentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    // 선택 안 된 상태에서도 텍스트 표시 (공간 충분함)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        tab.title,
                                        color = contentColor,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Deprecated: DashboardTabBar 내부로 로직 통합됨
}

// 탭 정의
enum class DashboardTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    RATIO("비율", Icons.Default.PieChart),
    HISTORY("분류", Icons.Default.List),
    LOG("로그", Icons.Default.Description)
}

// ==== 5. 하단 상세 위험 카드 ====
@Composable
fun DetailedRiskCard(
    title: String,
    riskDetail: RiskDetail,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    DashboardCard(backgroundColor = BackgroundLight) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 왼쪽 아이콘 원형 배경
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        fontSize = 15.sp
                    )
                    Text(
                        text = riskDetail.count.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 태그 칩
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    riskDetail.tags.take(2).forEach { tag ->
                        Surface(
                            color = color.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = color,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 프로그레스 바 (이미지 디자인과 동일하게)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val totalBars = 15
                    val filledBars = (riskDetail.count.toFloat() / 1000 * totalBars).toInt().coerceIn(0, totalBars)
                    
                    repeat(totalBars) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (index < filledBars) color else Color(0xFFE0E0E0)
                                )
                        )
                    }
                }
            }
        }
    }
}

// ==== 보호 상태 배지 (글래스모피즘) ====
@Composable
fun ProtectionStatusBadge(
    isProtected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isProtected) {
        Color(0xFF143D95) // 보호중 - 파란색
    } else {
        Color(0xFF5E0B0B) // 미보호 - 빨간색
    }
    
    val statusText = if (isProtected) "보호중" else "미보호"
    
    // 더 세련된 글래스모피즘 박스
    Box(
        modifier = modifier
            .height(40.dp)
            .width(100.dp)
    ) {
        // 배경 그림자 레이어 (깊이감 추가)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 120f
                    )
                )
        )
        
        // 메인 글래스 카드
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp),
            shape = RoundedCornerShape(26.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = 0.12f),
                                backgroundColor.copy(alpha = 0.06f)
                            )
                        )
                    )
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.02f)
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(300f, 300f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    // 아이콘 (배경 없음)
                    Icon(
                        imageVector = if (isProtected) Icons.Default.Shield else Icons.Default.Warning,
                        contentDescription = null,
                        tint = backgroundColor,
                        modifier = Modifier.size(22.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = backgroundColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
