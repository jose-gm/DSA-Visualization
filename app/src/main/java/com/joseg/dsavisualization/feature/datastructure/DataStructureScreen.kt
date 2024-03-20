package com.joseg.dsavisualization.feature.datastructure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joseg.dsavisualization.ui.component.ContentCard

@Composable
fun DataStructureScreen(
    dataStructureViewModel: DataStructureViewModel = viewModel()
) {
    val list by dataStructureViewModel.uiStateFlow.collectAsStateWithLifecycle()
    DataStructureScreen(list)
}

@Composable
fun DataStructureScreen(
    dataStructures: List<DataStructureViewModel.DataStructureUiState>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = dataStructures) { dataStructure ->
            ContentCard(
                description = dataStructure.title,
                imageResource = dataStructure.imageResource,
                onClick = { /*TODO*/ }
            )
        }
    }
}

@Preview
@Composable
fun DataStructureScreenPreview() {
    val viewModel: DataStructureViewModel = viewModel()
    val list by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    DataStructureScreen(dataStructures = list)
}