package com.neurotech.feature_tonic_info_impl

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.min

class ScaleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setShadowLayer(1f,0F,0F, Color.GRAY)
    }
    private val greenRectOne = RectF()
    private val greenRectTwo = RectF()
    private val greenRectThree = RectF()

    private val yellowRectOne = RectF()
    private val yellowRectTwo = RectF()
    private val yellowRectThree = RectF()

    private val redRectOne = RectF()
    private val redRectTwo = RectF()
    private val redRectThree = RectF()

    private val margin get() = (height - rectHeight*9)/9
    private val rectHeight get() = height/13
    private val rectWidth get() = width/4
    private val ledge get() = rectWidth / 6

     var value = 0
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        with(canvas){
            drawGreenScale()
            drawYellowScale()
            drawRedScale()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        greenRectOne.set(
            (0 + rectWidth).toFloat(),
            (height-rectHeight).toFloat(),
            (width - rectWidth).toFloat(),
            (height).toFloat()
        )
        greenRectTwo.set(
            greenRectOne.left,
            greenRectOne.top- margin - rectHeight,
            greenRectOne.right,
            greenRectOne.top-margin
        )
        greenRectThree.set(
            greenRectTwo.left,
            greenRectTwo.top-margin - rectHeight,
            greenRectTwo.right,
            greenRectTwo.top-margin
        )

        yellowRectOne.set(
            greenRectThree.left-ledge,
            greenRectThree.top - margin - rectHeight,
            greenRectThree.right+ledge,
            greenRectThree.top-margin
        )
        yellowRectTwo.set(
            yellowRectOne.left-ledge,
            yellowRectOne.top - margin - rectHeight,
            yellowRectOne.right+ledge,
            yellowRectOne.top-margin
        )
        yellowRectThree.set(
            yellowRectTwo.left-ledge,
            yellowRectTwo.top - margin - rectHeight,
            yellowRectTwo.right+ledge,
            yellowRectTwo.top-margin
        )
        redRectOne.set(
            yellowRectThree.left-ledge,
            yellowRectThree.top - margin - rectHeight,
            yellowRectThree.right+ledge,
            yellowRectThree.top-margin
        )
        redRectTwo.set(
            redRectOne.left-ledge,
            redRectOne.top - margin - rectHeight,
            redRectOne.right+ledge,
            redRectOne.top-margin
        )
        redRectThree.set(
            redRectTwo.left-ledge,
            redRectTwo.top - margin - rectHeight,
            redRectTwo.right+ledge,
            redRectTwo.top-margin
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 150
        val desiredHeight = 150

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredWidth, widthSize)
            }
            else -> {
                desiredWidth
            }
        }

        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredHeight, heightSize)
            }
            else -> {
                desiredHeight
            }
        }

        setMeasuredDimension(width, height)
    }

    private fun Canvas.drawGreenScale(){
        paint.color = when(value){
            in 1000..10000 -> ContextCompat.getColor(context, R.color.green_active)
            else -> ContextCompat.getColor(context, R.color.green_not_active)
        }
        drawRect(greenRectOne,paint)

        paint.color = when(value){
            in 2000..10000 -> ContextCompat.getColor(context, R.color.green_active)
            else -> ContextCompat.getColor(context, R.color.green_not_active)
        }
        drawRect(greenRectTwo,paint)

        paint.color = when(value){
            in 3000..10000 -> ContextCompat.getColor(context, R.color.green_active)
            else -> ContextCompat.getColor(context, R.color.green_not_active)
        }
        drawRect(greenRectThree,paint)
    }

    private fun Canvas.drawYellowScale(){
        paint.color = when(value){
            in 4000..10000 -> ContextCompat.getColor(context, R.color.yellow_active)
            else -> ContextCompat.getColor(context, R.color.yellow_not_active)
        }
        drawRect(yellowRectOne,paint)

        paint.color = when(value){
            in 5000..10000 -> ContextCompat.getColor(context, R.color.yellow_active)
            else -> ContextCompat.getColor(context, R.color.yellow_not_active)
        }
        drawRect(yellowRectTwo,paint)

        paint.color = when(value){
            in 6000..10000 -> ContextCompat.getColor(context, R.color.yellow_active)
            else -> ContextCompat.getColor(context, R.color.yellow_not_active)
        }
        drawRect(yellowRectThree,paint)
    }

    private fun Canvas.drawRedScale(){
        paint.color = when(value){
            in 7000..10000 -> ContextCompat.getColor(context, R.color.red_active)
            else -> ContextCompat.getColor(context, R.color.red_not_active)
        }
        drawRect(redRectOne,paint)

        paint.color = when(value){
            in 8000..10000 -> ContextCompat.getColor(context, R.color.red_active)
            else -> ContextCompat.getColor(context, R.color.red_not_active)
        }
        drawRect(redRectTwo,paint)

        paint.color = when(value){
            in 9000..10000 -> ContextCompat.getColor(context, R.color.red_active)
            else -> ContextCompat.getColor(context, R.color.red_not_active)
        }
        drawRect(redRectThree,paint)
    }
}