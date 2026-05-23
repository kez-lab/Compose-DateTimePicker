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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.PickerDefaults
import com.kez.picker.date.DatePicker
import com.kez.picker.date.rememberDatePickerState
import com.kez.picker.sample.getMonthContentDescription
import com.kez.picker.sample.getMonthName
import com.kez.picker.util.currentDate
import compose.icons.FeatherIcons
import compose.icons.feathericons.Calendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSampleScreen(
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "DatePicker Sample",
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
            val leapDate = remember(today) {
                nearestLeapDateOnOrAfter(date = today)
            }
            val allowedYears = remember(today.year, leapDate.year) {
                (today.year..leapDate.year).toList()
            }
            val allowedDays = remember(today.day, leapDate.day) {
                listOf(1, 15, today.day, leapDate.day).distinct().sorted()
            }
            val pickerItems = remember(allowedYears, allowedDays, today, leapDate) {
                PickerDefaults.datePickerItems(
                    yearItems = allowedYears,
                    dayItems = allowedDays,
                    minDate = today,
                    maxDate = leapDate
                )
            }
            val state = rememberDatePickerState(
                items = pickerItems,
                initialDate = today
            )
            var selectedDateText by rememberSaveable { mutableStateOf(today.toString()) }

            SelectedValueCard(
                icon = FeatherIcons.Calendar,
                label = "Selected date",
                value = selectedDateText,
                supportingText = "Range: $today..$leapDate · Days: ${allowedDays.joinToString()}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SampleActionRow {
                Button(
                    onClick = {
                        state.selectDate(today, pickerItems)
                        selectedDateText = state.selectedDate.toString()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = FeatherIcons.Calendar,
                        contentDescription = null
                    )
                    IconTextGap()
                    Text("Set $today")
                }

                OutlinedButton(
                    onClick = {
                        state.selectDate(leapDate, pickerItems)
                        selectedDateText = state.selectedDate.toString()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set $leapDate")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PickerPanel {
                DatePicker(
                    state = state,
                    onSelectedDateChange = { selectedDateText = it.toString() },
                    items = pickerItems,
                    display = PickerDefaults.datePickerDisplay(
                        yearItemText = { "${it}년" },
                        monthItemText = { getMonthName(it) },
                        dayItemText = { "${it}일" }
                    ),
                    style = PickerDefaults.style(
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
                        )
                    ),
                    accessibility = PickerDefaults.datePickerAccessibility(
                        yearPickerLabel = "연도",
                        monthPickerLabel = "월",
                        dayPickerLabel = "일",
                        yearItemContentDescription = { "${it}년" },
                        monthItemContentDescription = { getMonthContentDescription(it) },
                        dayItemContentDescription = { "${it}일" },
                        previousItemActionLabel = "이전 항목 선택",
                        nextItemActionLabel = "다음 항목 선택"
                    )
                )
            }
        }
    }
}

private fun nearestLeapDateOnOrAfter(date: LocalDate): LocalDate {
    var candidateYear = date.year
    while (true) {
        if (candidateYear.isLeapYear()) {
            val candidate = LocalDate(
                year = candidateYear,
                month = Month.FEBRUARY,
                day = 29
            )
            if (candidate >= date) return candidate
        }
        candidateYear += 1
    }
}

private fun Int.isLeapYear(): Boolean =
    this % 4 == 0 && (this % 100 != 0 || this % 400 == 0)
