package com.kez.picker.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.rememberPickerState
import com.kez.picker.time.TimePicker
import com.kez.picker.util.TimeFormat
import com.kez.picker.util.TimePeriod
import com.kez.picker.util.currentDate
import com.kez.picker.util.currentHour
import com.kez.picker.util.currentMinute

/**
 * 앱의 진입점 컴포저블
 */
@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PickerDemoScreen()
        }
    }
}

/**
 * 선택기 데모 화면
 */
@Composable
fun PickerDemoScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Compose Multiplatform DateTimePicker",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // 시간 선택기 섹션
        TimePickers()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 날짜 선택기 섹션
        DatePickers()
    }
}

/**
 * 시간 선택기 섹션
 */
@Composable
fun TimePickers() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "시간 선택기",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 12시간제 시간 선택기
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "12시간제",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                TimePicker(
                    hourPickerState = rememberPickerState(
                        if (currentHour > 12) currentHour - 12 else if (currentHour == 0) 12 else currentHour
                    ),
                    minutePickerState = rememberPickerState(currentMinute),
                    periodPickerState = rememberPickerState(if (currentHour >= 12) TimePeriod.PM else TimePeriod.AM),
                    timeFormat = TimeFormat.HOUR_12,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Gray),
                    selectedTextStyle = TextStyle(fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold),
                    pickerWidth = 60.dp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 24시간제 시간 선택기
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "24시간제",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                TimePicker(
                    hourPickerState = rememberPickerState(currentHour),
                    minutePickerState = rememberPickerState(currentMinute),
                    timeFormat = TimeFormat.HOUR_24,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Gray),
                    selectedTextStyle = TextStyle(fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold),
                    pickerWidth = 60.dp
                )
            }
        }
    }
}

/**
 * 날짜 선택기 섹션
 */
@Composable
fun DatePickers() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "날짜 선택기",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        YearMonthPicker(
            yearPickerState = rememberPickerState(currentDate.year),
            monthPickerState = rememberPickerState(currentDate.monthNumber),
            textStyle = TextStyle(fontSize = 14.sp, color = Color.Gray),
            selectedTextStyle = TextStyle(fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold),
            pickerWidth = 80.dp
        )
    }
} 