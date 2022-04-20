package com.ilovesudoku.sudokusolver.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ilovesudoku.sudokusolver.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignIdsToCells(view)
        setupNumberPad(view)
        setupNotesTakingButton(view)
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

    private fun setupNumberPad(view: View) {
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val numberPad = view.findViewById<TableRow>(R.id.number_key_pad)
        for (i in 0..8) {
            numberPad[i].setOnClickListener {
                viewModel.onNumberPadClick(i+1)
            }
        }
    }

    private fun setupNotesTakingButton(view: View) {
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val notesTakingButton = (view.findViewById<Button>(R.id.note_taking_state_button))

        viewModel.notesTakingMode.observe(
            viewLifecycleOwner
        ) {
            notesTakingButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null /* left */,
                ResourcesCompat.getDrawable(
                    resources,
                    if (it) R.drawable.ic_baseline_edit_24 else R.drawable.ic_baseline_edit_off_24,
                    null
                ),
                null /* right */,
                null /*bottom */
            )
        }
        notesTakingButton.setOnClickListener {
            viewModel.notesTakingMode.value = !viewModel.notesTakingMode.value!!
        }
    }

}
