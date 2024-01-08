package com.yusmp.plan.presentation.customView.diagramGant

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.content.ContextCompat
import com.yusmp.plan.R
import com.yusmp.plan.presentation.common.extentions.dpToPx
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.max

class DiagramGantView @JvmOverloads constructor(
    context: Context,
    attributesSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attributesSet, defStyleAttr, defStyleRes) {

    // region Paint

    // Для строк
    private val rowPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    // Для разделителей
    private val separatorsPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = SEPARATOR_STROKE_WIDTH_IN_DP.toFloat()
        color = SEPARATOR_DEFAULT_COLOR
    }

    // Для названий периодов
    private val periodNamePaint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.gant_period_name_text_size)
        color = PERIOD_NAME_TEXT_DEFAULT_COLOR
    }

    // Для фигур тасок
    private val taskShapePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    // Для названий тасок
    private val taskNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.gant_task_name_text_size)
        color = Color.WHITE
    }
    // endregion

    // region Размеры

    // Ширина столбца с периодом
    private val periodWidth = PERIOD_WIDTH_IN_DP.dpToPx

    // Высота строки
    private val rowHeight = ROW_HEIGHT_IN_DP.dpToPx

    // Общая ширина контента
    private val contentWidth: Int
        get() = periodWidth * periods.getValue(periodType).size

    // Радиус скругления углов таски
    private val taskCornerRadius = resources.getDimension(R.dimen.gant_task_corner_radius)

    // Вертикальный отступ таски внутри строки
    private val taskVerticalMargin = TASK_VERTICAL_MARGIN_IN_DP.dpToPx.toFloat()

    // Горизонтальный отступ текста таски внутри ее фигуры
    private val taskTextHorizontalMargin = TASK_TEXT_HORIZONTAL_MARGIN_IN_DP.dpToPx.toFloat()

    // Радиус круга, вырезаемого из фигуры таски
    private val cutOutRadius = (rowHeight - taskVerticalMargin * 2) / 4
    // endregion

    // region Цвета и инструменты

    // Rect для рисования строк
    private val rowRect = Rect()

    // Чередующиеся цвета строк
    private val rowColors = listOf(
        Color.LTGRAY,
        Color.WHITE
    )

    // Цвета градиента
    private val gradientStartColor = ContextCompat.getColor(context, R.color.blue_700)
    private val gradientEndColor = ContextCompat.getColor(context, R.color.blue_200)
    // endregion

    // region Вспомогательные сущности для обработки Touch эвентов

    // Значения последнего эвента
    private val lastPoint = PointF()
    private var lastPointerId = 0

    // Отвечает за зум и сдвиги
    private val transformations = Transformations()

    // Обнаружение и расчет скейла
    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())

    // endregion

    // region Время
    private var periodType = PeriodType.MONTH
    private val periods = initPeriods()
    // endregion

    // region Списки тасок

    private var tasks: List<Task> = emptyList()
    private var uiTasks: List<UiTask> = emptyList()
    // endregion

    fun setTasks(tasks: List<Task>) {
        if (tasks != this.tasks) {
            this.tasks = tasks
            uiTasks = tasks.map(::UiTask)
            updateTasksRects()

            requestLayout()
            invalidate()
        }
    }

    init {
        if (isInEditMode) {
            val now = LocalDate.now()
            tasks = listOf(
                Task(
                    name = "Task 1",
                    dateStart = now.minusMonths(1),
                    dateEnd = now
                ),
                Task(
                    name = "Task 2 long name",
                    dateStart = now.minusWeeks(2),
                    dateEnd = now.plusWeeks(1)
                ),
                Task(
                    name = "Task 3",
                    dateStart = now.minusMonths(2),
                    dateEnd = now.plusMonths(2)
                ),
                Task(
                    name = "Some Task 4",
                    dateStart = now.plusWeeks(2),
                    dateEnd = now.plusMonths(2).plusWeeks(1)
                ),
                Task(
                    name = "Task 5",
                    dateStart = now.minusMonths(2).minusWeeks(1),
                    dateEnd = now.plusWeeks(1)
                )
            )
            uiTasks = tasks.map(::UiTask)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        // Пример - Запрещаем родителю перехватывать тачи в текущем взаимодействии, если движение пальца горизонтальное
        if (abs(event.x - lastPoint.x) > abs(event.y - lastPoint.y)) {
            parent?.requestDisallowInterceptTouchEvent(true)
        }

        return if (event.pointerCount > 1) scaleGestureDetector.onTouchEvent(event) else processMove(
            event
        )
    }

    private fun processMove(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastPoint.set(event.x, event.y)
                lastPointerId = event.getPointerId(0)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                // Если размер контента меньше размера View - сдвиг недоступен
                if (width < contentWidth) {
                    val pointerId = event.getPointerId(0)
                    // Чтобы избежать скачков - сдвигаем, только если поинтер(палец) тот же, что и раньше
                    if (lastPointerId == pointerId) {
                        transformations.addTranslation(event.x - lastPoint.x)
                    }

                    // Запоминаем поинтер и последнюю точку в любом случае
                    lastPoint.set(event.x, event.y)
                    lastPointerId = event.getPointerId(0)

                    true
                } else {
                    false
                }
            }

            else -> false
        }
    }

    override fun onDraw(canvas: Canvas) = with(canvas) {
        drawRows()
        drawPeriods()
        drawTasks()
    }

    private fun Canvas.drawRows() {
        repeat(tasks.size + 1) { index ->
            // Rect для строки создан заранее, чтобы не создавать объекты во время отрисовки, но мы можем его подвигать
            rowRect.offsetTo(0, rowHeight * index)
            if (rowRect.top < height) {
                // Чередуем цвета строк
                rowPaint.color = rowColors[index % rowColors.size]
                drawRect(rowRect, rowPaint)
            }
        }
        // Разделитель между периодами и задачами
        val horizontalSeparatorY = rowHeight.toFloat()
        drawLine(0f, horizontalSeparatorY, width.toFloat(), horizontalSeparatorY, separatorsPaint)
    }

    private fun Canvas.drawPeriods() {
        val currentPeriods = periods.getValue(periodType)
        val nameY = periodNamePaint.getTextBaselineByCenter(rowHeight / 2f)
        currentPeriods.forEachIndexed { index, periodName ->
            // По X текст рисуется относительно его начала
            val textWidth = periodNamePaint.measureText(periodName)
            val periodCenter = periodWidth * transformations.scaleX * (index + 0.5f)
            val nameX = (periodCenter - textWidth / 2) + transformations.translationX
            drawText(periodName, nameX, nameY, periodNamePaint)
            // Разделитель
            val separatorX =
                periodWidth * (index + 1f) * transformations.scaleX + transformations.translationX
            drawLine(separatorX, 0f, separatorX, height.toFloat(), separatorsPaint)
        }
    }

    private fun Canvas.drawTasks() {
        val minTextLeft = taskTextHorizontalMargin
        uiTasks.forEach { uiTask ->
            if (uiTask.isRectOnScreen) {
                drawPath(uiTask.path, taskShapePaint)

                val taskRect = uiTask.rect
                val taskName = uiTask.task.name
                // Расположение названия
                val textStart =
                    (taskRect.left + cutOutRadius + taskTextHorizontalMargin).coerceAtLeast(
                        minTextLeft
                    )
                val maxTextWidth = taskRect.right - taskTextHorizontalMargin - textStart
                if (maxTextWidth > 0) {
                    val textY = taskNamePaint.getTextBaselineByCenter(taskRect.centerY())
                    // Количество символов из названия, которые поместятся в фигуру
                    val charsCount = taskNamePaint.breakText(taskName, true, maxTextWidth, null)
                    drawText(
                        taskName.substring(startIndex = 0, endIndex = charsCount),
                        textStart,
                        textY,
                        taskNamePaint
                    )
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rowRect.set(0, 0, w, rowHeight)

        taskShapePaint.shader = LinearGradient(
            0f,
            0f,
            w.toFloat(),
            0f,
            gradientStartColor,
            gradientEndColor,
            Shader.TileMode.CLAMP
        )

        updateTasksRects()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredWidth = max(minWidth, contentWidth)
        val desiredHeight = max(minHeight, rowHeight * (tasks.size + 1))

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    private fun Paint.getTextBaselineByCenter(center: Float) = center - (descent() + ascent()) / 2

    private fun updateTasksRects() {
        uiTasks.forEachIndexed { index, uiTask -> uiTask.updateInitialRect(index) }
        // Пересчитываем что необходимо и применяем предыдущие трансформации
        transformations.recalculate()
    }

    // region Сохранение состояния

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).also(transformations::onSaveInstanceState)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            transformations.onRestoreInstanceState(state)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    // endregion

    private fun initPeriods(): Map<PeriodType, List<String>> {
        val today = LocalDate.now()
        // Один раз получаем все названия периодов для каждого из PeriodType
        return PeriodType.values().associateWith { periodType ->
            val startDate = today.minusMonths(MONTH_COUNT)
            val endDate = today.plusMonths(MONTH_COUNT)
            var lastDate = startDate
            mutableListOf<String>().apply {
                while (lastDate <= endDate) {
                    add(periodType.getDateString(lastDate))
                    lastDate = periodType.increment(lastDate)
                }
            }
        }
    }

    private inner class UiTask(val task: Task) {
        // Rect с учетом всех преобразований
        val rect = RectF()

        // Path для фигуры таски
        val path = Path()

        // Path для вырезаемого круга
        val cutOutPath = Path()

        // Начальный Rect для текущих размеров View
        private val untransformedRect = RectF()

        // Если false, таск рисовать не нужно
        val isRectOnScreen: Boolean
            get() = rect.top < height && (rect.right > 0 || rect.left < width)

        fun updateInitialRect(index: Int) {
            fun getX(date: LocalDate): Float? {
                val periodIndex =
                    periods.getValue(periodType).indexOf(periodType.getDateString(date))
                return if (periodIndex >= 0) {
                    periodWidth * (periodIndex + periodType.getPercentOfPeriod(date))
                } else {
                    null
                }
            }

            untransformedRect.set(
                getX(task.dateStart) ?: -taskCornerRadius,
                rowHeight * (index + 1f) + taskVerticalMargin,
                getX(task.dateEnd) ?: (width + taskCornerRadius),
                rowHeight * (index + 2f) - taskVerticalMargin,
            )
            rect.set(untransformedRect)
        }

        fun transform(matrix: Matrix) {
            // Трансформируем untransformedRect и помещаем полученные значения в rect
            matrix.mapRect(rect, untransformedRect)
            updatePath()
        }

        private fun updatePath() {
            if (isRectOnScreen) {
                // Вырезаемый круг
                with(cutOutPath) {
                    reset()
                    addCircle(rect.left, rect.centerY(), cutOutRadius, Path.Direction.CW)
                }
                with(path) {
                    reset()
                    // Прямоугольник
                    addRoundRect(rect, taskCornerRadius, taskCornerRadius, Path.Direction.CW)
                    // Вырезаем из прямоугольника круг
                    op(cutOutPath, Path.Op.DIFFERENCE)
                }
            }
        }
    }

    private inner class Transformations {
        var translationX = 0f
            private set
        var scaleX = 1f
            private set

        // Матрица для преобразования фигур тасок
        private val matrix = Matrix()

        // На сколько максимально можно сдвинуть диаграмму
        private val minTranslation: Float
            get() = (width - contentWidth * transformations.scaleX).coerceAtMost(0f)

        // Относительный сдвиг на dx
        fun addTranslation(dx: Float) {
            translationX = (translationX + dx).coerceIn(minTranslation, 0f)
            transformTasks()
        }

        // Относительное увеличение на sx
        fun addScale(sx: Float) {
            scaleX = (scaleX * sx).coerceIn(1f, MAX_SCALE)
            recalculateTranslationX()
            updatePeriodTypeIfNeeded(scaleX)
            transformTasks()
        }

        fun onSaveInstanceState(state: SavedState) {
            state.translationX = translationX
            state.scaleX = scaleX
        }

        fun onRestoreInstanceState(state: SavedState) {
            translationX = state.translationX
            scaleX = state.scaleX
            recalculate()
        }

        // Пересчет необходимых значений и применение к таскам
        fun recalculate() {
            recalculateTranslationX()
            updatePeriodTypeIfNeeded(scaleX)
            transformTasks()
        }

        // Когда изменился scale или размер View надо пересчитать сдвиг
        private fun recalculateTranslationX() {
            translationX = translationX.coerceIn(minTranslation, 0f)
        }

        private fun transformTasks() {
            // Подготовка матрицы для трансформации фигур тасок
            with(matrix) {
                reset()
                // Порядок имеет значение
                setScale(scaleX, 1f)
                postTranslate(translationX, 0f)
            }
            uiTasks.forEach { it.transform(matrix) }
            invalidate()
        }

        // В зависимости от скейла PeriodType может измениться
        private fun updatePeriodTypeIfNeeded(scale: Float) {
            val periodTypes = PeriodType.values()
            // Шаг скейла, при котором меняется PeriodType
            val scaleStep = (MAX_SCALE - 1f) / periodTypes.size
            val periodTypeIndex =
                ((scale - 1f) / scaleStep).toInt().coerceAtMost(periodTypes.lastIndex)
            val periodType = periodTypes[periodTypeIndex]
            if (this@DiagramGantView.periodType != periodType) {
                this@DiagramGantView.periodType = periodType
                updateTasksRects()
            }
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return run {
                transformations.addScale(detector.scaleFactor)
                true
            }
        }
    }

    private class SavedState : BaseSavedState {
        var translationX: Float = 0f
        var scaleX: Float = 0f

        // Коснтруктор для сохранения стейта
        constructor(superState: Parcelable?) : super(superState)

        // Коснтруктор для восстановления стейта
        constructor(source: Parcel?) : super(source) {
            source?.apply {
                // Порядок имеет значение
                translationX = readFloat()
                scaleX = readFloat()
            }
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            // Порядок имеет значение
            out.writeFloat(translationX)
            out.writeFloat(scaleX)
        }

        companion object {
            // Как у любого Parcelable
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

    companion object {
        // Количество месяцев до и после текущей даты
        private const val MONTH_COUNT = 2L

        private const val MAX_SCALE = 2f

        // Размеры
        private const val PERIOD_WIDTH_IN_DP = 100

        private const val ROW_HEIGHT_IN_DP = 48

        private const val SEPARATOR_STROKE_WIDTH_IN_DP = 5

        // Отступы и скругления

        private const val TASK_VERTICAL_MARGIN_IN_DP = 4

        private const val TASK_TEXT_HORIZONTAL_MARGIN_IN_DP = 4

        // Цвета по умолчанию
        const val SEPARATOR_DEFAULT_COLOR = Color.GRAY

        const val PERIOD_NAME_TEXT_DEFAULT_COLOR = Color.DKGRAY
    }
}