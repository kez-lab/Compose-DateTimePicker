package com.kez.picker.sample

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.rememberPickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock

 */
@Composable
fun TimePickerScreen() {
    var selectedFormat by remember { mutableIntStateOf(0) }
    var showSelectedTime by remember { mutableStateOf(false) }

    // 모든 상태 미리 생성 및 공유
    val hour12State = rememberPickerState(
        if (currentHour > 12) currentHour - 12 else if (currentHour == 0) 12 else currentHour
    )
    val hour24State = rememberPickerState(currentHour)
    val minuteState = rememberPickerState(currentMinute)
    val periodState = rememberPickerState(if (currentHour >= 12) TimePeriod.PM else TimePeriod.AM)

    // 선택된 시간 텍스트
    val selectedTimeText = remember(
        hour12State.selectedItem, hour24State.selectedItem,
        minuteState.selectedItem, periodState.selectedItem, selectedFormat
    ) {
        if (selectedFormat == 0) {
            val period = periodState.selectedItem
            val hour = hour12State.selectedItem
            val minute = minuteState.selectedItem
            formatTime12(hour, minute, period)
        } else {
            val hour = hour24State.selectedItem
            val minute = minuteState.selectedItem
            formatTime24(hour, minute)
        }
    }

    LaunchedEffect(selectedFormat) {
        showSelectedTime = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 헤더
        Text(
            text = "시간 선택",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            fontWeight = FontWeight.Bold
        )

        // 탭 로우
        TabRow(
            selectedTabIndex = selectedFormat,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedFormat == 0,
                onClick = { selectedFormat = 0 },
                text = { Text("12시간제") }
            )
            Tab(
                selected = selectedFormat == 1,
                onClick = { selectedFormat = 1 },
                text = { Text("24시간제") }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 선택기 카드
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            // 공통 패딩과 정렬이 있는 컨테이너
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // AnimatedContent를 사용해 두 선택기 간 전환
                AnimatedContent(
                    targetState = selectedFormat,
                    transitionSpec = {
                        val direction = if (targetState > initialState) 1 else -1
                        slideInHorizontally { width -> direction * width } + fadeIn(
                            animationSpec = tween(durationMillis = 300)
                        ) togetherWith slideOutHorizontally { width -> -direction * width } + fadeOut(
                            animationSpec = tween(durationMillis = 300)
                        ) using SizeTransform(clip = false)
                    },
                    label = "TimePicker Format Animation"
                ) { format ->
                    // 고정된 크기의 컨테이너를 제공하여 애니메이션 중 레이아웃 변화 최소화
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp), // 충분한 고정 높이 지정
                        contentAlignment = Alignment.Center
                    ) {
                        if (format == 0) {
                            // 12시간제 타임피커
                            TimePicker(
                                hourPickerState = hour12State,
                                minutePickerState = minuteState,
                                periodPickerState = periodState,
                                timeFormat = TimeFormat.HOUR_12,
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                selectedTextStyle = TextStyle(
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                ),
                                dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                pickerWidth = 64.dp
                            )
                        } else {
                            // 24시간제 타임피커
                            TimePicker(
                                hourPickerState = hour24State,
                                minutePickerState = minuteState,
                                timeFormat = TimeFormat.HOUR_24,
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                selectedTextStyle = TextStyle(
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                ),
                                dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                pickerWidth = 64.dp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 확인 버튼
        Button(
            onClick = { showSelectedTime = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("시간 확인")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 선택된 시간 표시
        AnimatedVisibility(
            visible = showSelectedTime,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = FeatherIcons.Clock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "선택한 시간: $selectedTimeText",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

