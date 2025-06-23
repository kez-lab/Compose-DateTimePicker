package com.kez.picker.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.Picker
import com.kez.picker.rememberPickerState
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check

/**
 * 기본 Picker 컴포저블 예제 화면
 */
@Composable
fun BasicPickerScreen() {
    val items = listOf("Apple", "Banana", "Cherry", "Date", "Fig", "Grape", "Kiwi")
    val pickerState = rememberPickerState(items.first())
    val showSelected = remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "기본 Picker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Picker(
                    items = items,
                    state = pickerState,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    selectedTextStyle = TextStyle(
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    ),
                    dividerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showSelected.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(FeatherIcons.Check, contentDescription = null)
                Spacer(modifier = Modifier.height(4.dp))
                Text("선택 확인")
            }

            if (showSelected.value) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "선택한 항목: ${pickerState.selectedItem}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

