package com.ilovesudoku.sudokusolver.ui.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.ilovesudoku.sudokusolver.R
import kotlin.math.abs

class CellView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs), View.OnClickListener {
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.main_grid_cell_view, this, true)
        setOnClickListener(this)
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
            cellHeight = (measuredWidth * 1.1).toInt()
        }

        setMeasuredDimension(cellWidth, cellHeight)

        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY)
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY)
        getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun onClick(v: View?) {
        println("--------------- cell Clicked: $cellId")
    }
}
