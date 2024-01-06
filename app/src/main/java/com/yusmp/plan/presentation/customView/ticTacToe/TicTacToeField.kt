package com.yusmp.plan.presentation.customView.ticTacToe

data class TicTacToeField(
    val rows: Int,
    val columns: Int,
) {
    private val cells = MutableList(rows) { MutableList(columns) { CellType.EMPTY } }

    val listeners = mutableListOf<OnTicTacToeFieldChangedListener>()

    fun getCell(row: Int, column: Int) =
        if ((row < 0) or (column < 0) or (row >= rows) or (column >= columns)) CellType.EMPTY
        else cells[row][column]

    fun setCell(row: Int, column: Int, cell: CellType) {
        if ((row < 0) or (column < 0) or (row >= rows) or (column >= columns)) return

        if (cells[row][column] != cell) {
            cells[row][column] = cell
            listeners?.forEach { it?.invoke(this) }
        }
    }

}

typealias OnTicTacToeFieldChangedListener = (field: TicTacToeField) -> Unit