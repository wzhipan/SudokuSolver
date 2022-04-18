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
        val storeOwner = findViewTreeViewModelStoreOwner()
        val viewModel = storeOwner?.let { ViewModelProvider(it)[MainViewModel::class.java] }
            ?: return
        viewModel.cellValues.observe(lifecycleOwner, Observer {
            refreshCellView(viewModel)
            if (!viewModel.cellEditable[cellId]) {
                updateMainCellNumberColor(R.color.black)
                isClickable = false
            }
        })
        viewModel.candidateValues[cellId].observe(lifecycleOwner, Observer {
            refreshCandidateNumbersView(viewModel)
        })
    }

    private fun updateMainCellNumberColor(colorResId: Int) {
        val mainCellTextView = findViewById<TextView>(R.id.main_cell_text)
        mainCellTextView.setTextColor(resources.getColor(colorResId, null))
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

    override fun onClick(v: View?) {
        println("--------------- cell Clicked: $cellId")
    }
}
