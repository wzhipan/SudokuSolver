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
        val row = cellId / 9
        val col = cellId % 9
        val blockRow = row / 3
        val blockCol = col / 3
        for (i in 0..8) {
            val rowValue = cellValues.value?.get(9 * row + i) ?: 0
            if (rowValue in 1..9) {
                cellValuesAvailable[rowValue - 1] = false
            }
            val colValue = cellValues.value?.get(9 * i + col) ?: 0
            if (colValue in 1..9) {
                cellValuesAvailable[colValue - 1] = false
            }
            val blockValue =
                cellValues.value?.get((blockRow * 3 + i / 3) * 9 + blockCol * 3 + i % 3) ?: 0
            if (blockValue in 1..9) {
                cellValuesAvailable[blockValue - 1] = false
            }
        }

        return cellValuesAvailable
    }
}
