package com.joseg.dsavisualization.feature.datastructure

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.joseg.dsavisualization.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DataStructureViewModel : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(listOf(
        DataStructureUiState(
            "Linked list",
            R.drawable.linked_list,
            DataStructureType.lINKED_LIST
        ),
        DataStructureUiState(
            "Stack",
            R.drawable.stack,
            DataStructureType.STACK
        ),
        DataStructureUiState(
            "Queue",
            R.drawable.queue,
            DataStructureType.QUEUE
        )
    ))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    data class DataStructureUiState(
        val title: String,
        @DrawableRes val imageResource: Int,
        val type: DataStructureType
    )

    enum class DataStructureType {
        lINKED_LIST,
        STACK,
        QUEUE,
        ARRAY,
        GRAPH,
        BINARY_TREE;
    }
}