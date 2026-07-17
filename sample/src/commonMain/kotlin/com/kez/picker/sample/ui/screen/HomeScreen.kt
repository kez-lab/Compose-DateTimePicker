package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kez.picker.sample.ui.navigation.Screen
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowRight
import compose.icons.feathericons.Calendar
import compose.icons.feathericons.CheckCircle
import compose.icons.feathericons.Clock
import compose.icons.feathericons.Layers
import compose.icons.feathericons.Minus
import compose.icons.feathericons.Square

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Compose DateTimePicker",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MenuListItem(
                    title = "WheelPicker Sample",
                    description = "Live value updates plus a settled callback",
                    icon = FeatherIcons.Layers,
                    modifier = Modifier.testTag("sample-menu-wheel-picker"),
                    onClick = { navController.navigate(Screen.WheelPicker.route) }
                )
            }
            item {
                MenuListItem(
                    title = "Integrated Sample",
                    description = "Tabs with shared date and time state",
                    icon = FeatherIcons.CheckCircle,
                    modifier = Modifier.testTag("sample-menu-integrated"),
                    onClick = { navController.navigate(Screen.Integrated.route) }
                )
            }
            item {
                MenuListItem(
                    title = "TimePicker Sample",
                    description = "12-hour and 24-hour state updates",
                    icon = FeatherIcons.Clock,
                    modifier = Modifier.testTag("sample-menu-time-picker"),
                    onClick = { navController.navigate(Screen.TimePicker.route) }
                )
            }
            item {
                MenuListItem(
                    title = "YearMonthPicker Sample",
                    description = "Month selection with programmatic reset",
                    icon = FeatherIcons.Calendar,
                    modifier = Modifier.testTag("sample-menu-year-month-picker"),
                    onClick = { navController.navigate(Screen.YearMonthPicker.route) }
                )
            }
            item {
                MenuListItem(
                    title = "DatePicker Sample",
                    description = "Custom year range and leap-day target",
                    icon = FeatherIcons.Calendar,
                    modifier = Modifier.testTag("sample-menu-date-picker"),
                    onClick = { navController.navigate(Screen.DatePicker.route) }
                )
            }
            item {
                MenuListItem(
                    title = "DateRangePicker Sample",
                    description = "Ordered start and end date selection",
                    icon = FeatherIcons.Calendar,
                    modifier = Modifier.testTag("sample-menu-date-range-picker"),
                    onClick = { navController.navigate(Screen.DateRangePicker.route) }
                )
            }
            item {
                MenuListItem(
                    title = "BottomSheet Sample",
                    description = "Committed value plus draft sheet state",
                    icon = FeatherIcons.Layers,
                    modifier = Modifier.testTag("sample-menu-bottom-sheet"),
                    onClick = { navController.navigate(Screen.BottomSheet.route) }
                )
            }
            item {
                MenuListItem(
                    title = "Background Style",
                    description = "Divider-free picker styling",
                    icon = FeatherIcons.Square,
                    modifier = Modifier.testTag("sample-menu-background-style"),
                    onClick = { navController.navigate(Screen.BackgroundStyle.route) }
                )
            }
        }
    }
}


@Composable
internal fun MenuListItem(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(FeatherIcons.ArrowRight, contentDescription = null)
        }
    }
}
