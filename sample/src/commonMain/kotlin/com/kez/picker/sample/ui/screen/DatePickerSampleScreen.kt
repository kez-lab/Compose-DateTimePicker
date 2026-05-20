package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.PickerDefaults
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.sample.getMonthContentDescription
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Calendar
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSampleScreen(
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DatePicker Sample") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = FeatherIcons.ArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val state = rememberDatePickerState()
            val demoDate = LocalDate(2026, 5, 20)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                DatePicker(
                    state = state,
                    visibleItemsCount = 3,
                    textStyles = PickerDefaults.textStyles(
                        textStyle = MaterialTheme.typography.bodyLarge,
                        selectedTextStyle = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ),
                    colors = PickerDefaults.colors(
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    yearPickerLabel = "연도",
                    monthPickerLabel = "월",
                    dayPickerLabel = "일",
                    yearItemContentDescription = { "${it}년" },
                    monthItemContentDescription = { getMonthContentDescription(it) },
                    dayItemContentDescription = { "${it}일" },
                    previousItemActionLabel = "이전 항목 선택",
                    nextItemActionLabel = "다음 항목 선택"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { state.selectDate(demoDate) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = FeatherIcons.Calendar,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set 2026-05-20")
                }

                OutlinedButton(
                    onClick = { state.selectDate(LocalDate(2024, 2, 29)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set 2024-02-29")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Display Selected Value
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Selected Date",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = FeatherIcons.Calendar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.selectedDate.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
