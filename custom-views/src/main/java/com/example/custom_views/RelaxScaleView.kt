package com.example.custom_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.min

class RelaxScaleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {


    private val scalePath = Path()
    private val rect = RectF()
    private val rectProgress = RectF()

    private val red = ContextCompat.getColor(context, R.color.red_active)
    private val green = ContextCompat.getColor(context, R.color.green_active)

    private val scaleWidth = 50F
    private val beginLineWidth = 70F
    private val scaleLineWidth = 40F

    private val viewWidth get() = scaleWidth * 2 + beginLineWidth * 2


    private val progressPaint = Paint().apply {
        style = Paint.Style.FILL
        alpha = 70
    }

    private val scalePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 7f
    }

    private val linePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 5F
    }

    private val beginLinePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.GRAY
        strokeWidth = 5F
        alpha = 70
    }

    private val textPaint = Paint().apply {
        textSize = 60f
    }

    var value: Int = 3000
        set(value) {
            field = value
            invalidate()
        }

    var beginValue: Int = 5000
        set(value) {
            field = value
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {


        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val widthRect = viewWidth
        val heightRect = 400

        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(widthRect.toInt(), widthSize)
            }
            else -> {
                widthRect.toInt()
            }
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(heightRect, heightSize)
            }
            else -> {
                heightRect
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(beginLineWidth, 50F, beginLineWidth+scaleWidth*2, height.toFloat() - 50)
        scalePath.addRoundRect(rect, 2000f, height.toFloat(), Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawScale()
        canvas.drawScaleLine(value)
        canvas.drawBeginLine(beginValue)
        canvas.drawProgressRect(beginValue, value)
    }

    private fun Canvas.drawProgressRect(beginValue: Int, currentValue: Int) {
        val bValue = rangeConvert(beginValue)
        val cValue = rangeConvert(currentValue)
        clipPath(scalePath)
        rectProgress.apply {
            if (beginValue > currentValue) {
                set(rect.left+4, bValue, rect.right - 4, cValue)
                progressPaint.color = green
            } else {
                set(rect.left+4, cValue, rect.right- 4, bValue)
                progressPaint.color = red
            }
        }
        drawRect(rectProgress, progressPaint)
    }

    private fun Canvas.drawScale() {
        drawPath(scalePath, scalePaint)
    }


    private fun Canvas.drawBeginLine(value: Int) {
        val yValue = rangeConvert(value)
        drawLine(
            rect.left - beginLineWidth,
            yValue,
            rect.right + beginLineWidth,
            yValue,
            beginLinePaint
        )
    }

    private fun Canvas.drawScaleLine(value: Int) {
        val yValue = rangeConvert(value)
        val color = if (value < beginValue) {
            green
        } else {
            red
        }
        linePaint.color = color
        textPaint.color = color
        drawLine(rect.left - scaleLineWidth, yValue, rect.right + scaleLineWidth, yValue, linePaint)
    }

    private fun rangeConvert(value: Int): Float {
        val newValue: Float = (value * (abs(rect.top - rect.bottom))) / 10000f
        return rect.bottom - newValue
    }
}