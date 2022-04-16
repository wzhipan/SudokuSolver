package com.ilovesudoku.sudokusolver.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ilovesudoku.sudokusolver.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        println("--------------- MainFragment onActivityCreated: viewModel created: ${viewModel.initialBoard.value?.get(0)}" )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignIdsToCells(view)
    }

    private fun assignIdsToCells(view: View) {
        val tableLayout = view.findViewById<TableLayout>(R.id.sudoku_layout)
        for (i in 0..2) {
            val tableRow = tableLayout.getChildAt(i) as TableRow
            for (j in 0..2) {
                val subTable = tableRow[j] as TableLayout
                for (k in 0..2) {
                    val subTableRow = subTable[k] as TableRow
                    for (l in 0..2) {
                        (subTableRow[l] as CellView).cellId = (3 * i + k) * 9 + j * 3 + l
                    }
                }
            }
        }
    }

}
