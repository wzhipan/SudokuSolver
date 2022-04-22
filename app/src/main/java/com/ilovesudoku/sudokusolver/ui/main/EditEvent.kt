package com.ilovesudoku.sudokusolver.ui.main

enum class EditEventType {
    FILL,
    DELETE,
}

enum class EditValueType {
    MAIN_CELL_NUMBER,
    CANDIDATE_NUMBER,
}

data class EditEvent(
    val eventType: EditEventType,
    val valueType: EditValueType,
    val cellId: Int,
    val value: Int,
    val oldValue: Int? = null
)
