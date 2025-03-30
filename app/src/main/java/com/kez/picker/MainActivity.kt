package com.kez.picker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kez.picker.date.DatePickerDialog
import com.kez.picker.date.DateRangePickerDialog
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.sample.R
import com.kez.picker.time.TimePickerDialog
import com.kez.picker.ui.theme.ComposePickerTheme
import com.kez.picker.ui.theme.DividerColor
import com.kez.picker.ui.theme.Primary
import com.kez.picker.ui.theme.SelectedTextColor
import com.kez.picker.ui.theme.UnselectedTextColor
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.currentDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ComposePickerTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    stringResource(R.string.app_title),
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Primary
                            )
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Dialog visibility states
                    val isShowTimePicker = remember { mutableStateOf(false) }
                    val isShowYearMonthPicker = remember { mutableStateOf(false) }
                    val isShowDatePickerDialog = remember { mutableStateOf(false) }
                    val isShowDateRangePickerDialog = remember { mutableStateOf(false) }
                    val isShow24HourTimePicker = remember { mutableStateOf(false) }

                    // 기본 문자열 값을 설정하고 나중에 사용
                    val noTimeSelected = stringResource(R.string.no_time_selected)
                    val noDateSelected = stringResource(R.string.no_date_selected)
                    val noDateRangeSelected = stringResource(R.string.no_date_range_selected)
                    val no24hTimeSelected = stringResource(R.string.no_24h_time_selected)

                    // Selected dates and times for display
                    val selectedTime = remember { mutableStateOf(noTimeSelected) }
                    val selectedDate = remember { mutableStateOf(noDateSelected) }
                    val selectedDateRange = remember { mutableStateOf(noDateRangeSelected) }
                    val selected24HourTime = remember { mutableStateOf(no24hTimeSelected) }

                    // Context for getString() 호출 사용
                    val context = LocalContext.current

                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Banner
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Primary.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.library_title),
                                        style = TextStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = Primary
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = stringResource(R.string.library_description),
                                        style = TextStyle(
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Title
                        item {
                            Text(
                                text = stringResource(R.string.component_examples),
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Time Picker Section
                        item {
                            PickerCard(
                                title = stringResource(R.string.time_picker_12h_title),
                                description = stringResource(R.string.time_picker_12h_desc),
                                selectedValue = selectedTime.value,
                                buttonText = stringResource(R.string.open_12h_time_picker),
                                onClick = { isShowTimePicker.value = true },
                                badgeText = stringResource(R.string.basic)
                            )
                        }

                        // 24-hour Time Picker Section
                        item {
                            PickerCard(
                                title = stringResource(R.string.time_picker_24h_title),
                                description = stringResource(R.string.time_picker_24h_desc),
                                selectedValue = selected24HourTime.value,
                                buttonText = stringResource(R.string.open_24h_time_picker),
                                onClick = { isShow24HourTimePicker.value = true },
                                badgeText = stringResource(R.string.advanced)
                            )
                        }

                        // Date Picker Section
                        item {
                            PickerCard(
                                title = stringResource(R.string.date_picker_title),
                                description = stringResource(R.string.date_picker_desc),
                                selectedValue = selectedDate.value,
                                buttonText = stringResource(R.string.open_date_picker),
                                onClick = { isShowDatePickerDialog.value = true },
                                badgeText = stringResource(R.string.basic)
                            )
                        }

                        // Date Range Picker Section
                        item {
                            PickerCard(
                                title = stringResource(R.string.date_range_picker_title),
                                description = stringResource(R.string.date_range_picker_desc),
                                selectedValue = selectedDateRange.value,
                                buttonText = stringResource(R.string.open_date_range_picker),
                                onClick = { isShowDateRangePickerDialog.value = true },
                                badgeText = stringResource(R.string.advanced)
                            )
                        }

                        // YearMonth Picker without Dialog
                        item {
                            PickerCard(
                                title = stringResource(R.string.year_month_picker_title),
                                description = stringResource(R.string.year_month_picker_desc),
                                selectedValue = "",
                                buttonText = stringResource(R.string.show_year_month_picker),
                                onClick = { isShowYearMonthPicker.value = true },
                                badgeText = stringResource(R.string.custom)
                            )
                        }

                        // GitHub link
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.github_link),
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }

                    // Dialog implementations
                    if (isShowTimePicker.value) {
                        TimePickerDialog(
                            modifier = Modifier.wrapContentSize(),
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true
                            ),
                            timeFormat = TimeFormat.HOUR_12,
                            dividerColor = DividerColor,
                            selectedTextStyle = TextStyle(
                                fontSize = 22.sp,
                                color = SelectedTextColor,
                                fontWeight = FontWeight.Bold
                            ),
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = UnselectedTextColor
                            ),
                            titleText = stringResource(R.string.select_time_12h),
                            onDismissRequest = {
                                isShowTimePicker.value = false
                            },
                            onDoneClickListener = { localDateTime ->
                                isShowTimePicker.value = false
                                val hour12 =
                                    if (localDateTime.hour % 12 == 0) 12 else localDateTime.hour % 12
                                val amPm = if (localDateTime.hour < 12) "AM" else "PM"
                                val timeFormatted = "$hour12:${
                                    localDateTime.minute.toString().padStart(2, '0')
                                } $amPm"
                                selectedTime.value =
                                    context.getString(R.string.selected_time, timeFormatted)
                                Toast.makeText(
                                    this@MainActivity,
                                    selectedTime.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    if (isShow24HourTimePicker.value) {
                        TimePickerDialog(
                            modifier = Modifier.wrapContentSize(),
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true
                            ),
                            timeFormat = TimeFormat.HOUR_24,
                            dividerColor = DividerColor,
                            selectedTextStyle = TextStyle(
                                fontSize = 22.sp,
                                color = SelectedTextColor,
                                fontWeight = FontWeight.Bold
                            ),
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = UnselectedTextColor
                            ),
                            titleText = stringResource(R.string.select_time_24h),
                            onDismissRequest = {
                                isShow24HourTimePicker.value = false
                            },
                            onDoneClickListener = { localDateTime ->
                                isShow24HourTimePicker.value = false
                                val timeFormatted = "${
                                    localDateTime.hour.toString().padStart(2, '0')
                                }:${localDateTime.minute.toString().padStart(2, '0')}"
                                selected24HourTime.value =
                                    context.getString(R.string.selected_time, timeFormatted)
                                Toast.makeText(
                                    this@MainActivity,
                                    selected24HourTime.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    if (isShowYearMonthPicker.value) {
                        Dialog(
                            onDismissRequest = {
                                isShowYearMonthPicker.value = false
                            },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true
                            )
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = stringResource(R.string.select_year_month),
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    YearMonthPicker(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        dividerColor = DividerColor,
                                        selectedTextStyle = TextStyle(
                                            fontSize = 22.sp,
                                            color = SelectedTextColor,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        textStyle = TextStyle(
                                            fontSize = 16.sp,
                                            color = UnselectedTextColor
                                        )
                                    )

                                    Button(
                                        onClick = { isShowYearMonthPicker.value = false },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                                    ) {
                                        Text(stringResource(R.string.close), color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    if (isShowDatePickerDialog.value) {
                        DatePickerDialog(
                            modifier = Modifier.wrapContentSize(),
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true
                            ),
                            dividerColor = DividerColor,
                            selectedTextStyle = TextStyle(
                                fontSize = 22.sp,
                                color = SelectedTextColor,
                                fontWeight = FontWeight.Bold
                            ),
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = UnselectedTextColor
                            ),
                            titleText = stringResource(R.string.select_date),
                            onDismissRequest = {
                                isShowDatePickerDialog.value = false
                            },
                            onDoneClickListener = { localDate ->
                                isShowDatePickerDialog.value = false
                                val dateFormatted = "${localDate.year}-${
                                    localDate.monthNumber.toString().padStart(2, '0')
                                }-${localDate.dayOfMonth.toString().padStart(2, '0')}"
                                selectedDate.value =
                                    context.getString(R.string.selected_date, dateFormatted)
                                Toast.makeText(
                                    this@MainActivity,
                                    selectedDate.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    if (isShowDateRangePickerDialog.value) {
                        val today = currentDate
                        val nextMonth = today.plus(DatePeriod(months = 1))

                        DateRangePickerDialog(
                            modifier = Modifier.wrapContentSize(),
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true
                            ),
                            initialStartDate = today,
                            initialEndDate = nextMonth,
                            dividerColor = DividerColor,
                            selectedTextStyle = TextStyle(
                                fontSize = 22.sp,
                                color = SelectedTextColor,
                                fontWeight = FontWeight.Bold
                            ),
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = UnselectedTextColor
                            ),
                            onDismissRequest = {
                                isShowDateRangePickerDialog.value = false
                            },
                            onDoneClickListener = { startDate, endDate ->
                                isShowDateRangePickerDialog.value = false

                                val startFormatted = "${startDate.year}-${
                                    startDate.monthNumber.toString().padStart(2, '0')
                                }-${startDate.dayOfMonth.toString().padStart(2, '0')}"
                                val endFormatted = "${endDate.year}-${
                                    endDate.monthNumber.toString().padStart(2, '0')
                                }-${endDate.dayOfMonth.toString().padStart(2, '0')}"

                                selectedDateRange.value = context.getString(
                                    R.string.selected_range,
                                    startFormatted,
                                    endFormatted
                                )

                                Toast.makeText(
                                    this@MainActivity,
                                    selectedDateRange.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PickerCard(
    title: String,
    description: String,
    selectedValue: String,
    buttonText: String,
    onClick: () -> Unit,
    badgeText: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                if (badgeText.isNotEmpty()) {
                    val badgeColor = when (badgeText) {
                        context.getString(R.string.basic) -> Color(0xFF4CAF50) // Green
                        context.getString(R.string.advanced) -> Color(0xFF2196F3) // Blue
                        else -> Color(0xFFF44336) // Red for Custom
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = badgeColor,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Text(
                text = description,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            )

            if (selectedValue.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = selectedValue,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                    )
                }
            }

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = buttonText,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}