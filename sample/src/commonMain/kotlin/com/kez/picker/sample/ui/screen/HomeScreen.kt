package com.kez.picker.sample.ui.screen

import androidx.compose.foundation.clickable
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding).padding(16.dp)
        ) {
            item {
                MenuListItem(
                    title = "Integrated Sample",
                    description = "Date and time picker combined",
                    icon = FeatherIcons.CheckCircle,
                    onClick = { navController.navigate(Screen.Integrated.route) }
                )
            }
            item {
                MenuListItem(
                    title = "TimePicker Sample",
                    description = "Standalone TimePicker component",
                    icon = FeatherIcons.Clock,
                    onClick = { navController.navigate(Screen.TimePicker.route) }
                )
            }
            item {
                MenuListItem(
                    title = "DatePicker Sample",
                    description = "Standalone YearMonthPicker component",
                    icon = FeatherIcons.Calendar,
                    onClick = { navController.navigate(Screen.DatePicker.route) }
                )
            }
            item {
                MenuListItem(
                    title = "BottomSheet Sample",
                    description = "Date/time selection in bottom sheet",
                    icon = FeatherIcons.Layers,
                    onClick = { navController.navigate(Screen.BottomSheet.route) }
                )
            }
            item {
                MenuListItem(
                    title = "Background Style",
                    description = "Picker with background design",
                    icon = FeatherIcons.Square,
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
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
                Text(description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Icon(FeatherIcons.ArrowRight, contentDescription = "Navigate")
        }
    }
}
