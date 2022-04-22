package com.ilovesudoku.sudokusolver.ui.main

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val cellEditable: BooleanArray = BooleanArray(81)
    val candidateValues: Array<MutableLiveData<BooleanArray>> =
        Array(81) { MutableLiveData(BooleanArray(9) { false }) }
    val cellValues: MutableLiveData<IntArray> =
        MutableLiveData<IntArray>().also {
            loadInitialCellValues(it)
        }
    val notesTakingMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val undoStackLiveData: MutableLiveData<ArrayDeque<EditEvent>> =
        MutableLiveData(ArrayDeque())
    val redoStackLiveData: MutableLiveData<ArrayDeque<EditEvent>> =
        MutableLiveData(ArrayDeque())
    val isNumPadEnabled: MutableLiveData<Boolean> = MutableLiveData(false)

    private val selectedCell: MutableLiveData<Int> = MutableLiveData()

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

    private fun getCellValuesAvailable(cellId: Int): BooleanArray {
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

    fun isNumberUsedByRelatedCell(cellId: Int): Boolean {
        val cellNum = cellValues.value?.get(cellId) ?: return false
        for (i in 0..80) {
            if (isRelatedCell(cellId, i) && cellNum == cellValues.value?.get(i)) {
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
        val selectedCellId = selectedCell.value ?: return
        if (!cellEditable[selectedCellId]) return
        if (notesTakingMode.value == true) {
            if (fillCandidateNumber(selectedCellId, numberClicked)) {
                addUndoEvent(
                    EditEvent(
                        EditEventType.FILL,
                        EditValueType.CANDIDATE_NUMBER,
                        selectedCellId,
                        numberClicked
                    )
                )
            }
        } else {
            val oldValue = cellValues.value?.get(selectedCellId)
            if (fillMainCellNumber(selectedCellId, numberClicked)) {
                addUndoEvent(
                    EditEvent(
                        EditEventType.FILL,
                        EditValueType.MAIN_CELL_NUMBER,
                        selectedCellId,
                        numberClicked,
                        oldValue
                    )
                )
            }
        }
    }

    private fun addUndoEvent(undoEvent: EditEvent) {
        val undoStack = undoStackLiveData.value ?: ArrayDeque()
        undoStack.addLast(undoEvent)
        undoStackLiveData.value = undoStack
        //reset redo stack
        redoStackLiveData.value = ArrayDeque()
    }

    private fun fillCandidateNumber(selectedCellId: Int, numberToFill: Int): Boolean {
        if (selectedCellId in 0..80 && !selectedCellHasMainCellNumber()) {
            val candidateNumbers =
                candidateValues[selectedCellId].value ?: BooleanArray(9) { false }
            // no need to fill if the candidate number is already set
            if (candidateNumbers[numberToFill - 1]) {
                return false
            }
            candidateNumbers[numberToFill - 1] = true
            candidateValues[selectedCellId].value = candidateNumbers
            return true
        }
        return false
    }

    private fun deleteCandidateNumber(selectedCellId: Int, numberToDelete: Int): Boolean {
        if (selectedCellId in 0..80) {
            val candidateNumbers =
                candidateValues[selectedCellId].value ?: return false
            // no need to delete if the candidate number is not set
            if (!candidateNumbers[numberToDelete - 1]) {
                return false
            }
            candidateNumbers[numberToDelete - 1] = false
            candidateValues[selectedCellId].value = candidateNumbers
            return true
        }
        return false
    }

    private fun fillMainCellNumber(selectedCellId: Int, numberToFill: Int): Boolean {
        if (selectedCellId !in 0..80) {
            return false
        }
        val cellValuesToUpdate = cellValues.value ?: return false
        // no need to fill if the main cell number equals to the number to fill
        if (cellValuesToUpdate[selectedCellId] == numberToFill) {
            return false
        }
        cellValuesToUpdate[selectedCellId] = numberToFill
        cellValues.value = cellValuesToUpdate
        return true
    }

    fun undo() {
        val undoStack = undoStackLiveData.value ?: ArrayDeque()
        if (undoStack.isEmpty()) {
            return
        }

        val lastEditEvent = undoStack.removeLast()
        undoStackLiveData.value = undoStack
        if (lastEditEvent.eventType == EditEventType.FILL) {
            if (lastEditEvent.valueType == EditValueType.MAIN_CELL_NUMBER) {
                if (!fillMainCellNumber(lastEditEvent.cellId, lastEditEvent.oldValue ?: 0)) {
                    return
                }
            } else if (lastEditEvent.valueType == EditValueType.CANDIDATE_NUMBER) {
                if (!deleteCandidateNumber(lastEditEvent.cellId, lastEditEvent.value)) {
                    return
                }
            }
        }

        val redoStack = redoStackLiveData.value ?: ArrayDeque()
        redoStack.addLast(lastEditEvent)
        redoStackLiveData.value = redoStack
    }

    fun redo() {
        val redoStack = redoStackLiveData.value ?: ArrayDeque()
        if (redoStack.isEmpty()) {
            return
        }

        val lastEditEvent = redoStack.removeLast()
        redoStackLiveData.value = redoStack
        if (lastEditEvent.eventType == EditEventType.FILL) {
            if (lastEditEvent.valueType == EditValueType.MAIN_CELL_NUMBER) {
                if (!fillMainCellNumber(lastEditEvent.cellId, lastEditEvent.value)) {
                    return
                }
            } else if (lastEditEvent.valueType == EditValueType.CANDIDATE_NUMBER) {
                if (!fillCandidateNumber(lastEditEvent.cellId, lastEditEvent.value)) {
                    return
                }
            }
        }

        val undoStack = undoStackLiveData.value ?: ArrayDeque()
        undoStack.addLast(lastEditEvent)
        undoStackLiveData.value = undoStack
    }

    fun isSameNumberAsSelectedCell(cellId: Int): Boolean {
        val selectedCellId = selectedCell.value ?: return false
        return selectedCellId != cellId && cellValues.value?.get(cellId) == cellValues.value?.get(
            selectedCellId
        )
    }

    fun isSelectedCell(cellId: Int): Boolean {
        return cellId == selectedCell.value
    }

    private fun selectedCellHasMainCellNumber(): Boolean {
        val selectedCellId = selectedCell.value
        return selectedCellId == null || cellValues.value?.get(selectedCellId) != 0
    }

    fun observeSelectedCell(owner: LifecycleOwner, observer: Observer<Int>) {
        selectedCell.observe(owner, observer)
    }

    fun setSelectedCell(cellId: Int) {
        selectedCell.value = cellId
        isNumPadEnabled.value = cellEditable[cellId]
    }
}
