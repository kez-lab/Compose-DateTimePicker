package com.kez.picker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kez.picker.ui.theme.ComposePickerTheme
import com.kez.picker.util.TimeFormat

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
                        Dialog(
                            onDismissRequest = {
                                isShowTimePicker.value = false
                            },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true
                            )
                        ) {
                            TimePicker(
                                modifier = Modifier.wrapContentSize(),
                                timeFormat = TimeFormat.HOUR_12
                            )
                        }
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
