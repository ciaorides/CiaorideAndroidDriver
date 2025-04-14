package com.ciaorides.ciaorides.utils.barchart
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class BarGraphView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var barData: List<Pair<String, Float>> = emptyList()
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val axisColor = Color.BLACK
    private val textColor = Color.BLACK
    private val barColor = ContextCompat.getColor(context, com.ciaorides.ciaorides.R.color.appBlue)
    private val xAxisLabelPadding = 10f
    private val yAxisLabelPadding = 66f
    private val barSpacing = 30f
    private var maxYValue = 0f

    private val maxBarWidth = 100f

    init {
        barPaint.color = barColor
        textPaint.color = textColor
        textPaint.textSize = 24f
        axisPaint.color = axisColor
        axisPaint.strokeWidth = 2f
    }

    fun setData(data: List<Pair<String, Float>>) {
        barData = data
        maxYValue = data.maxOfOrNull { it.second } ?: 0f
        invalidate() // Request a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (barData.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()

        // Calculate available drawing area, considering padding for labels
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom - 2 * xAxisLabelPadding

        // Calculate the ideal bar width based on available space and spacing
        val idealBarWidth = (availableWidth - (barData.size - 1) * barSpacing) / barData.size

        // Determine the actual bar width, ensuring it doesn't exceed maxBarWidth
        val barWidth = idealBarWidth.coerceAtMost(maxBarWidth)

        // Draw Y-axis
        canvas.drawLine(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            paddingLeft.toFloat(),
            paddingTop + availableHeight,
            axisPaint
        )

        // Draw X-axis
        canvas.drawLine(
            paddingLeft.toFloat(),
            paddingTop + availableHeight,
            paddingLeft + availableWidth,
            paddingTop + availableHeight,
            axisPaint
        )

        // Draw Y-axis labels
        val numberOfYAxisLabels = 5 // Adjust as needed
        for (i in 0..numberOfYAxisLabels) {
            val value = maxYValue * (1 - i.toFloat() / numberOfYAxisLabels)
            val y = paddingTop + availableHeight * (i.toFloat() / numberOfYAxisLabels)
            val text = String.format("%.2f", value) // Format the amount
            val textBounds = Rect()
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            canvas.drawText(
                text,
                paddingLeft - yAxisLabelPadding - textBounds.width(),
                y + textBounds.height() / 2f,
                textPaint
            )
            // Draw small tick marks on Y-axis
            canvas.drawLine(
                paddingLeft.toFloat(),
                y,
                paddingLeft.toFloat() + 10f,
                y,
                axisPaint
            )
        }

        // Draw bars and X-axis labels
        barData.forEachIndexed { index, (label, value) ->
            val x = paddingLeft + index * (barWidth + barSpacing)
            val barHeight = (value / maxYValue) * availableHeight
            val y = paddingTop + availableHeight - barHeight

            // Draw the bar
            canvas.drawRect(x, y, x + barWidth, paddingTop + availableHeight, barPaint)

            // Draw X-axis label
            val textBounds = Rect()
            textPaint.getTextBounds(label, 0, label.length, textBounds)
            canvas.drawText(
                label,
                x + barWidth / 2f - textBounds.width() / 2f,
                paddingTop + availableHeight + xAxisLabelPadding + textBounds.height(),
                textPaint
            )

            // Draw value on top of the bar
            val valueText = String.format("%.2f", value)
            val valueTextBounds = Rect()
            textPaint.getTextBounds(valueText, 0, valueText.length, valueTextBounds)
            canvas.drawText(
                valueText,
                x + barWidth / 2f - valueTextBounds.width() / 2f,
                y - 8f, // Adjust vertical position above the bar
                textPaint
            )
        }
    }
}