package com.yusmp.plan.presentation.customView.ticTacToe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.yusmp.domain.common.extentions.isZero
import com.yusmp.plan.R
import com.yusmp.plan.presentation.common.extentions.dpToPx
import kotlin.math.max
import kotlin.math.min

typealias OnCellClickListener = (row: Int, column: Int, field: TicTacToeField) -> Unit

class TicTacToeView @JvmOverloads constructor(
    context: Context,
    attributesSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.TicTacToeFieldStyle,
    defStyleRes: Int = R.style.TicTacToeFieldDefaultStyle,
) : View(context, attributesSet, defStyleAttr, defStyleRes) {

    var ticTacToeField: TicTacToeField? = null
        set(value) {
            field?.listeners?.remove(listener)
            field = value
            value?.listeners?.add(listener)
            updateViewSizes()
//            requestLayout() вызывается, так как ticTacToeField влияет и на размер вью
            requestLayout()
            invalidate()
        }

    var onCellClickListener: OnCellClickListener? = null

    private val listener: OnTicTacToeFieldChangedListener = { ticTacToeField ->
        invalidate()
    }

    // region color
    private var crossColor = CROSS_DEFAULT_COLOR
    private var zeroColor = ZERO_DEFAULT_COLOR
    private var gridColor = GRID_DEFAULT_COLOR
    // endregion

    // region default param
    private val fieldRect = RectF()
    private val cellRect = RectF()
    private var cellSize = 0f
    private var cellPadding = 0f
    // endregion

    // region paint
    private val crossPaint = Paint().apply {
        isAntiAlias = true
        color = crossColor
        style = Paint.Style.STROKE
        strokeWidth = CROSS_PAINT_STROKE_WIDTH.dpToPx
    }

    private val zeroPaint = Paint().apply {
        isAntiAlias = true
        color = zeroColor
        style = Paint.Style.STROKE
        strokeWidth = ZERO_PAINT_STROKE_WIDTH.dpToPx
    }

    private val gridPaint = Paint().apply {
        isAntiAlias = true
        color = gridColor
        style = Paint.Style.STROKE
        strokeWidth = GRID_PAINT_STROKE_WIDTH.dpToPx
    }
    // endregion

    init {
        if (attributesSet != null) {
            initAttributes(attributesSet, defStyleAttr, defStyleRes)
        } else {
            initDefaultColors()
        }
        if (isInEditMode) {
            ticTacToeField = TicTacToeField(8, 6)
            ticTacToeField?.setCell(3, 5, CellType.CROSS)
            ticTacToeField?.setCell(3, 4, CellType.ZERO)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val field = this.ticTacToeField ?: return false
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                val row = getRow(event)
                val column = getColumn(event)

                return if (row >= 0 && column >= 0 && row < field.rows && column < field.columns) {
                    onCellClickListener?.invoke(row, column, field)
                    true
                } else {
                    false
                }
            }
        }
        return false
    }

    private fun getRow(event: MotionEvent): Int {
        return ((event.y - fieldRect.top) / cellSize).toInt()
    }

    private fun getColumn(event: MotionEvent): Int {
        return ((event.x - fieldRect.left) / cellSize).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        ticTacToeField ?: return
        if (cellSize.isZero()) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return

        drawGrid(canvas)
        drawCells(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val field = this.ticTacToeField ?: return

        val horizontalLineStartX = fieldRect.left
        val horizontalLineEndX = fieldRect.right

        (0..field.rows).map {
            val verticalLineY = fieldRect.top + cellSize * it
            canvas.drawLine(
                horizontalLineStartX,
                verticalLineY,
                horizontalLineEndX,
                verticalLineY,
                gridPaint
            )
        }

        val verticalLineStartY = fieldRect.top
        val verticalLineEndY = fieldRect.bottom

        (0..field.columns).map {
            val verticalLineX = fieldRect.left + cellSize * it
            canvas.drawLine(
                verticalLineX,
                verticalLineStartY,
                verticalLineX,
                verticalLineEndY,
                gridPaint
            )
        }
    }

    private fun drawCells(canvas: Canvas) {
        val field = this.ticTacToeField ?: return

        (0 until field.rows).map { row ->
            (0 until field.columns).map { column ->

                when (field.getCell(row = row, column = column)) {
                    CellType.CROSS -> {
                        drawCross(canvas, row, column)
                    }

                    CellType.ZERO -> {
                        drawZero(canvas, row, column)
                    }

                    CellType.EMPTY -> {
                        Unit
                    }
                }
            }
        }
    }

    private fun drawCross(canvas: Canvas, row: Int, column: Int) {
        val cellRect = getCellRect(row = row, column = column)
        canvas.drawLine(cellRect.left, cellRect.top, cellRect.right, cellRect.bottom, crossPaint)
        canvas.drawLine(cellRect.left, cellRect.bottom, cellRect.right, cellRect.top, crossPaint)
    }

    private fun drawZero(canvas: Canvas, row: Int, column: Int) {
        val cellRect = getCellRect(row = row, column = column)

        canvas.drawCircle(cellRect.centerX(), cellRect.centerY(), cellRect.width() / 2, zeroPaint)
    }

    private fun getCellRect(row: Int, column: Int): RectF {
        cellRect.top = fieldRect.top + row * cellSize + cellPadding
        cellRect.bottom = cellRect.top + cellSize - cellPadding * 2
        cellRect.left = fieldRect.left + column * cellSize + cellPadding
        cellRect.right = cellRect.left + cellSize - cellPadding * 2
        return cellRect
    }

    //    Определяем размеры всей вью
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPx = DESIRED_CELL_SIZE.dpToPx

        val rows = ticTacToeField?.rows ?: 0
        val columns = ticTacToeField?.columns ?: 0

        val desiredWidth = max(minWidth, columns * desiredCellSizeInPx + paddingLeft + paddingRight)
        val desiredHeight = max(minHeight, rows * desiredCellSizeInPx + paddingTop + paddingBottom)

        setMeasuredDimension(
//            resolveSize выбирает размеры: либо наш, либо из measureSpec.
//            Зависит от ограничений (в т.ч. WRAP_CONTENT, MATCH_PARENT, 50dp, 50px и т.д.).
//            Часто алгоритм resolveSize пишется вручную, можно этого не делать
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSizes()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ticTacToeField?.listeners?.add(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ticTacToeField?.listeners?.remove(listener)
    }

    private fun updateViewSizes() {
        val field = this.ticTacToeField ?: return

        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWidth, cellHeight)
        cellPadding = cellSize * 0.2f

//        Размеры самого игрового поля, так как игровое поле может быть меньше безопасной зоны из-за пропорций
        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    private fun initAttributes(attributesSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(
            attributesSet,
            R.styleable.TicTacToeView,
            defStyleAttr,
            defStyleRes
        )

        crossColor = typedArray.getColor(R.styleable.TicTacToeView_crossColor, CROSS_DEFAULT_COLOR)
        zeroColor = typedArray.getColor(R.styleable.TicTacToeView_zeroColor, ZERO_DEFAULT_COLOR)
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, GRID_DEFAULT_COLOR)

        typedArray.recycle()
    }

    //    Эти цвета инициализируются, если не указаны: xml атрибуты -> глобальный стиль -> стиль по умолчанию - последняя очередь
    private fun initDefaultColors() {
        crossColor = CROSS_DEFAULT_COLOR
        zeroColor = ZERO_DEFAULT_COLOR
        gridColor = GRID_DEFAULT_COLOR
    }

    companion object {
        const val CROSS_DEFAULT_COLOR = Color.BLACK
        const val ZERO_DEFAULT_COLOR = Color.RED
        const val GRID_DEFAULT_COLOR = Color.GREEN

        const val DESIRED_CELL_SIZE = 50

        const val CROSS_PAINT_STROKE_WIDTH = 3f
        const val ZERO_PAINT_STROKE_WIDTH = 3f
        const val GRID_PAINT_STROKE_WIDTH = 1f
    }
}