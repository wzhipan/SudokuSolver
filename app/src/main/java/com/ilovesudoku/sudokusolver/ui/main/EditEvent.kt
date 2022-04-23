package com.ilovesudoku.sudokusolver.ui.main

enum class EditEventType {
    FILL_MAIN_CELL_NUMBER,
    FILL_CANDIDATE_NUMBER,
    DELETE_CANDIDATE_NUMBERS,
}

class EditEvent(
    val eventType: EditEventType,
    val cellId: Int,
    val value: Int?,
    val oldMainCellValue: Int? = null,
    val oldCandidateValues: BooleanArray? = null,
)
