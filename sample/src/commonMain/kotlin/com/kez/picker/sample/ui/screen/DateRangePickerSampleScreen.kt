package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kez.picker.PickerDefaults
import com.kez.picker.date.DateRange
import com.kez.picker.date.DateRangePicker
import com.kez.picker.date.rememberDateRangePickerState
import com.kez.picker.sample.getMonthContentDescription
import com.kez.picker.sample.getMonthName
import com.kez.picker.util.currentDate
import compose.icons.FeatherIcons
import compose.icons.feathericons.Calendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRangePickerSampleScreen(
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "DateRangePicker Sample",
                onBackPressed = onBackPressed
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
            val today = remember { currentDate() }
            val yearStart = remember(today.year) { LocalDate(today.year, 1, 1) }
            val yearEnd = remember(today.year) { LocalDate(today.year, 12, 31) }
            val monthEndDay = remember(today) {
                when (today.month.number) {
                    2 -> 28
                    4, 6, 9, 11 -> 30
                    else -> 31
                }
            }
            val monthStart = remember(today) {
                LocalDate(today.year, today.month, 1)
            }
            val monthEnd = remember(today, monthEndDay) {
                LocalDate(today.year, today.month, monthEndDay)
            }
            val todayRange = remember(today) {
                DateRange(startDate = today, endDate = today)
            }
            val monthRange = remember(monthStart, monthEnd) {
                DateRange(startDate = monthStart, endDate = monthEnd)
            }
            val items = remember(today.year, yearStart, yearEnd) {
                PickerDefaults.datePickerItems(
                    yearItems = listOf(today.year),
                    minDate = yearStart,
                    maxDate = yearEnd
                )
            }
            val state = rememberDateRangePickerState(
                items = items,
                initialDateRange = todayRange
            )
            var selectedRangeText by rememberSaveable {
                mutableStateOf("${today}..${today}")
            }

            SelectedValueCard(
                icon = FeatherIcons.Calendar,
                label = "Selected range",
                value = selectedRangeText,
                supportingText = "Selectable year: ${today.year}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SampleActionRow {
                Button(
                    onClick = {
                        state.selectDateRange(
                            dateRange = monthRange,
                            items = items
                        )
                        selectedRangeText = state.selectedDateRange.asText()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = FeatherIcons.Calendar,
                        contentDescription = null
                    )
                    IconTextGap()
                    Text("This month")
                }

                OutlinedButton(
                    onClick = {
                        state.selectDateRange(
                            dateRange = todayRange,
                            items = items
                        )
                        selectedRangeText = state.selectedDateRange.asText()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Today only")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PickerPanel {
                DateRangePicker(
                    state = state,
                    onSelectedDateRangeChange = { selectedRangeText = it.asText() },
                    items = items,
                    display = PickerDefaults.datePickerDisplay(
                        yearItemText = { "${it}년" },
                        monthItemText = { getMonthName(it) },
                        dayItemText = { "${it}일" }
                    ),
                    spacingBetweenPickers = 8.dp,
                    startLabel = { Text("시작일") },
                    endLabel = { Text("종료일") },
                    accessibility = PickerDefaults.dateRangePickerAccessibility(
                        start = PickerDefaults.datePickerAccessibility(
                            yearPickerLabel = "시작 연도",
                            monthPickerLabel = "시작 월",
                            dayPickerLabel = "시작 일",
                            monthItemContentDescription = { getMonthContentDescription(it) }
                        ),
                        end = PickerDefaults.datePickerAccessibility(
                            yearPickerLabel = "종료 연도",
                            monthPickerLabel = "종료 월",
                            dayPickerLabel = "종료 일",
                            monthItemContentDescription = { getMonthContentDescription(it) }
                        )
                    )
                )
            }
        }
    }
}

private fun DateRange.asText(): String =
    "$startDate..$endDate"
