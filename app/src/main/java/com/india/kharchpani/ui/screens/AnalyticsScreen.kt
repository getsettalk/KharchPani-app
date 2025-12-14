package com.india.kharchpani.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.india.kharchpani.ui.composables.KharchPaniTopAppBar
import com.india.kharchpani.ui.viewmodel.ChartData
import com.india.kharchpani.ui.viewmodel.HomeUiState
import com.india.kharchpani.ui.viewmodel.MainViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun AnalyticsScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { KharchPaniTopAppBar(title = "Analytics") },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = { /* Already on analytics screen */ }) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics")
                    }
                    IconButton(onClick = { navController.navigate("export_import") }) {
                        Icon(Icons.Default.ImportExport, contentDescription = "Export/Import")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HomeUiState.Success -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (state.weeklyChartData.isNotEmpty()) {
                            Text(text = "This Week's Expenses", style = MaterialTheme.typography.titleMedium)
                            WeeklyChart(state.weeklyChartData)
                        }
                        if (state.monthlyChartData.isNotEmpty()) {
                            Text(text = "This Month's Expenses", style = MaterialTheme.typography.titleMedium)
                            MonthlyChart(state.monthlyChartData)
                        }
                        if (state.weeklyChartData.isEmpty() && state.monthlyChartData.isEmpty()) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(text = "No data available for charts.")
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun WeeklyChart(chartData: List<ChartData>) {
    val chartEntryModel = ChartEntryModelProducer(chartData.mapIndexed { index, data -> entryOf(index, data.amount) })
    val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        chartData[value.toInt()].label
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Chart(
            chart = columnChart(),
            chartModelProducer = chartEntryModel,
            startAxis = startAxis(),
            bottomAxis = bottomAxis(valueFormatter = horizontalAxisValueFormatter),
        )
    }
}

@Composable
private fun MonthlyChart(chartData: List<ChartData>) {
    val chartEntryModel = ChartEntryModelProducer(chartData.mapIndexed { index, data -> entryOf(index, data.amount) })
    val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        chartData[value.toInt()].label
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Chart(
            chart = columnChart(),
            chartModelProducer = chartEntryModel,
            startAxis = startAxis(),
            bottomAxis = bottomAxis(valueFormatter = horizontalAxisValueFormatter),
        )
    }
}
