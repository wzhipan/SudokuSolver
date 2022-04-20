package com.ilovesudoku.sudokusolver.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val cellEditable: BooleanArray = BooleanArray(81)
    val candidateValues: Array<MutableLiveData<BooleanArray>> =
        Array(81) { MutableLiveData(BooleanArray(9) { false }) }
    val cellValues: MutableLiveData<IntArray> =
        MutableLiveData<IntArray>().also {
            loadInitialCellValues(it)
        }
    val selectedCell: MutableLiveData<Int> = MutableLiveData()
    val notesTakingMode: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun loadInitialCellValues(mutableLiveData: MutableLiveData<IntArray>) {
        val initialBoardArray = intArrayOf(
            5, 3, 0, 0, 7, 0, 0, 0, 0,
            6, 0, 0, 1, 9, 5, 0, 0, 0,
            0, 9, 8, 0, 0, 0, 0, 6, 0,
            8, 0, 0, 0, 6, 0, 0, 0, 3,
            4, 0, 0, 8, 0, 3, 0, 0, 1,
            7, 0, 0, 0, 2, 0, 0, 0, 6,
            0, 6, 0, 0, 0, 0, 2, 8, 0,
            0, 0, 0, 4, 1, 9, 0, 0, 5,
            0, 0, 0, 0, 8, 0, 0, 7, 9
        )
        mutableLiveData.value = initialBoardArray

        for (i in 0..80) {
            cellEditable[i] = initialBoardArray[i] == 0
        }
    }

    fun getCellValuesAvailable(cellId: Int): BooleanArray {
        val cellValuesAvailable = BooleanArray(9) { true }
        for (i in 0..80) {
            if (isRelatedCell(cellId, i)) {
                val number = cellValues.value?.get(i) ?: 0
                if (number in 1..9) {
                    cellValuesAvailable[number - 1] = false
                }
            }
        }

        return cellValuesAvailable
    }

    fun getCandidateValues(cellId: Int): BooleanArray {
        val candidateValues = candidateValues[cellId].value ?: BooleanArray(9) { false }
        val availableValues = getCellValuesAvailable(cellId)
        return candidateValues.onEachIndexed { i, value -> value && availableValues[i] }
    }

    fun isRelatedCellSelected(currentCellId: Int): Boolean {
        val selectedCellId = selectedCell.value ?: return false
        return isRelatedCell(currentCellId, selectedCellId)
    }

    fun isNumberUsedByRelatedCell(cellId: Int) : Boolean {
        val cellNum = cellValues.value?.get(cellId) ?: return false
        for (i in 0..80) {
            if (isRelatedCell(cellId, i) && cellNum == cellValues.value?.get(i) ) {
                return true
            }
        }
        return false
    }

    private fun isRelatedCell(cellId: Int, compareCellId: Int?): Boolean {
        if (compareCellId == null || cellId == compareCellId) {
            return false
        }
        val cellRow = cellId / 9
        val compareCellRow = compareCellId / 9
        val cellCol = cellId % 9
        val compareCellCol = compareCellId % 9
        return cellRow == compareCellRow ||
                cellCol == compareCellCol ||
                (cellRow / 3 == compareCellRow / 3 && cellCol / 3 == compareCellCol / 3)
    }

    fun onNumberPadClick(numberClicked: Int) {
        if (notesTakingMode.value == true) {
            val selectedCellId = selectedCell.value ?: -1
            if (selectedCellId in 0..80) {
                val candidateNumbers =
                    candidateValues[selectedCellId].value ?: BooleanArray(9) { false }
                candidateNumbers[numberClicked - 1] = true
                candidateValues[selectedCellId].value = candidateNumbers
            }
        } else {
            val cellValuesToUpdate = cellValues.value ?: return
            cellValuesToUpdate[selectedCell.value ?: return] = numberClicked
            cellValues.value = cellValuesToUpdate
        }
    }

    fun isSameNumberAsSelectedCell(cellId: Int): Boolean {
        val selectedCellId = selectedCell.value ?: return false
        return selectedCellId != cellId && cellValues.value?.get(cellId) == cellValues.value?.get(
            selectedCellId
        )
    }
}
