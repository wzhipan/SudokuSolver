package com.ilovesudoku.sudokusolver.ui.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.get
import androidx.lifecycle.*
import com.ilovesudoku.sudokusolver.R
import kotlin.math.abs

class CellView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs), View.OnClickListener, DefaultLifecycleObserver {
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.main_grid_cell_view, this, true)
        setOnClickListener(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewTreeLifecycleOwner()?.lifecycle?.addObserver(this)
    }

    companion object {
        var cellWidth: Int = 0
        var cellHeight: Int = 0
    }

    var cellId: Int = -1

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (cellWidth == 0 || abs(measuredWidth.toDouble() / cellWidth - 1) > 0.01) {
            cellWidth = measuredWidth
            cellHeight = measuredWidth
        }

        setMeasuredDimension(cellWidth, cellHeight)

        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY)
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY)
        getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun onCreate(lifecycleOwner: LifecycleOwner) {
        super.onCreate(lifecycleOwner)
        val viewModel = getViewModel() ?: return
        viewModel.cellValues.observe(lifecycleOwner, Observer {
            refreshCellView(viewModel)
            updateMainCellValueTextColor(viewModel)
            if (!viewModel.cellEditable[cellId]) {
                isClickable = false
            }
        })
        viewModel.candidateValues[cellId].observe(lifecycleOwner, Observer {
            refreshCandidateNumbersView(viewModel)
        })
        viewModel.selectedCell.observe(
            lifecycleOwner,
            Observer { handleCellViewSelected(viewModel) })
    }

    private fun getViewModel(): MainViewModel? {
        val storeOwner = findViewTreeViewModelStoreOwner()
        return storeOwner?.let { ViewModelProvider(it)[MainViewModel::class.java] }
    }

    private fun refreshCellView(viewModel: MainViewModel) {
        val mainCellNumber = viewModel.cellValues.value?.get(cellId) ?: 0
        val mainCellTextView = findViewById<TextView>(R.id.main_cell_text)
        if (mainCellNumber > 9 || mainCellNumber < 1) {
            mainCellTextView.visibility = INVISIBLE
        } else {
            mainCellTextView.visibility = VISIBLE
            mainCellTextView.text = mainCellNumber.toString()
        }
        refreshCandidateNumbersView(viewModel)
    }

    private fun refreshCandidateNumbersView(viewModel: MainViewModel) {
        val mainCellNumber = viewModel.cellValues.value?.get(cellId) ?: 0
        val candidateNumbersView = findViewById<TableLayout>(R.id.candidate_numbers)
        candidateNumbersView.visibility =
            if (mainCellNumber > 9 || mainCellNumber < 1) VISIBLE else INVISIBLE
        val cellValuesAvailable = viewModel.getCellValuesAvailable(cellId)
        for (i in 0..8) {
            val tableRow = candidateNumbersView[i / 3] as TableRow
            val textView = tableRow[i % 3]
            textView.visibility = if (cellValuesAvailable[i]) VISIBLE else INVISIBLE
        }
    }

    private fun handleCellViewSelected(viewModel: MainViewModel) {
        updateCellBackground(viewModel)
        updateMainCellValueTextColor(viewModel)
    }

    private fun updateMainCellValueTextColor(viewModel: MainViewModel) {
        val mainCellTextView = findViewById<TextView>(R.id.main_cell_text)
        val textColorResId: Int = when {
            viewModel.isNumberUsedByRelatedCell(cellId) -> {
                R.color.error_number_used_by_related_cell_text_color
            }
            viewModel.cellEditable[cellId] -> {
                R.color.editable_cell_text_color
            }
            else -> {
                R.color.uneditable_cell_text_color
            }
        }

        mainCellTextView.setTextColor(
            resources.getColor(
                textColorResId,
                null
            )
        )
    }

    private fun updateCellBackground(viewModel: MainViewModel) {
        when {
            viewModel.selectedCell.value == cellId -> {
                setBackgroundColor(
                    resources.getColor(
                        R.color.cell_selected_background,
                        null
                    )
                )
            }
            viewModel.isRelatedCellSelected(cellId) -> {
                setBackgroundColor(
                    resources.getColor(
                        R.color.related_cell_selected_background_color,
                        null
                    )
                )
            }
            else -> {
                setBackgroundColor(0)
            }
        }
    }

    override fun onClick(v: View?) {
        println("--------------- cell Clicked: ${cellId / 9}:${cellId % 9}")
        val viewModel = getViewModel() ?: return
        viewModel.selectedCell.value = cellId
    }
}
