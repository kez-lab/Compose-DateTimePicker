package com.kez.picker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kez.picker.date.YearMonthPicker
import com.kez.picker.time.TimePickerDialog
import com.kez.picker.ui.theme.ComposePickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposePickerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val isShowTimePicker = remember { mutableStateOf(false) }
                    val isShowYearMonthPicker = remember { mutableStateOf(false) }
                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
                        item {
                            Button(
                                onClick = {
                                    isShowTimePicker.value = true
                                }
                            ) {
                                Text("Show TimePicker Dialog")
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    isShowYearMonthPicker.value = true
                                }
                            ) {
                                Text("Show YearMonthPicker Dialog")
                            }
                        }

                    }

                    if (isShowTimePicker.value) {
                        TimePickerDialog(
                            modifier = Modifier.wrapContentSize(),
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true
                            ),
                            dividerColor = Color.Gray,
                            onDismissRequest = {
                                isShowTimePicker.value = false
                            },
                            onDoneClickListener = { localDateTime ->
                                isShowTimePicker.value = false
                                Toast.makeText(
                                    this@MainActivity,
                                    "Selected Time: $localDateTime",
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
                            YearMonthPicker(
                                modifier = Modifier.wrapContentSize()
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
