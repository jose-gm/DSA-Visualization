package com.joseg.dsavisualization.feature.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.joseg.dsavisualization.feature.datastructure.DataStructureScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var state by remember { mutableStateOf(MainScreenTabs.DATA_STRUCTURE) }

    Column {
        CenterAlignedTopAppBar(
            title = { Text(text = "DSA Visualization") },
        )
        PrimaryTabRow(selectedTabIndex = state.index) {
            Tab(
                selected = true,
                onClick = { state = MainScreenTabs.DATA_STRUCTURE },
                text = { Text(text = "Data structure") }
            )
            Tab(
                selected = false,
                onClick = { state = MainScreenTabs.ALGORITHM },
                text = { Text(text = "Algorithm") }
            )
        }

        when (state) {
            MainScreenTabs.DATA_STRUCTURE -> DataStructureScreen()
            MainScreenTabs.ALGORITHM -> {}
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}

enum class MainScreenTabs(val index: Int) {
    DATA_STRUCTURE(0),
    ALGORITHM(1);
}