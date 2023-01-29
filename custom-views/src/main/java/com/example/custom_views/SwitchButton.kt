package com.example.custom_views

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.abs
import kotlin.math.min

class SwitchButton(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val path = Path()

    private val textPaint = Paint().apply {
        this.textSize = resources.getDimension(R.dimen.default_text_size)
    }

    private val basePaint = Paint().apply {
        color = Color.GRAY
        alpha = 50
        style = Paint.Style.FILL
    }

    private val switchPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val baseRect = RectF()
    private val switchRect = RectF()
    private val heightView = 100F

    private val textPosition get() =  width/3F

    private var leftText = "День"
    private var centerText = "Неделя"
    private var rightText = "Месяц"
    private val widthView = textPaint.measureText(leftText) +
            textPaint.measureText(centerText) +
            textPaint.measureText(rightText) + 30 * 3
    private var _duration = 300L

    private val _state = MutableStateFlow(1)
    val state: Flow<Int> get() = _state

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.SwitchButton,0,0).apply {
            try {
                basePaint.color = getColor(R.styleable.SwitchButton_backgroundColor, Color.GRAY)
                basePaint.alpha = getInt(R.styleable.SwitchButton_backgroundAlpha, 50)
                switchPaint.color = getColor(R.styleable.SwitchButton_selectedBackgroundColor, Color.RED)
                textPaint.textSize = getDimension(R.styleable.SwitchButton_textSize, 30f)
                _duration = getInt(R.styleable.SwitchButton_animationSpeed, 300).toLong()
            }finally {
                recycle()
            }

        }
    }

    fun setText(left:String, center:String, right:String){
        leftText = left
        centerText = center
        rightText = right
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = widthView.toInt()
        val desiredHeight = heightView.toInt()

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




    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?: return false
        if(event.action == MotionEvent.ACTION_DOWN){
            if(event.x < textPosition){
                _state.value = 1
                val leftAnim = ValueAnimator.ofFloat(switchRect.left ,0F).apply {
                    duration = _duration
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        switchRect.set(it.animatedValue as Float,height/2-heightView/2,switchRect.right,height/2+heightView/2)
                        invalidate()
                    }
                }
                val rightAnim = ValueAnimator.ofFloat(switchRect.right, width/3F).apply {
                    duration = _duration
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        switchRect.set(switchRect.left,height/2-heightView/2,it.animatedValue as Float,height/2+heightView/2)
                        invalidate()
                    }
                }
                AnimatorSet().apply {
                    play(leftAnim).with(rightAnim)
                    start()
                }
            }else if (event.x > textPosition && event.x < textPosition*2){
                _state.value = 2
                val leftAnim = ValueAnimator.ofFloat(switchRect.left ,textPosition).apply {
                    duration = _duration
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        switchRect.set(it.animatedValue as Float,height/2-heightView/2,switchRect.right,height/2+heightView/2)
                        invalidate()
                    }
                }
                val rightAnim = ValueAnimator.ofFloat(switchRect.right, textPosition*2).apply {
                    duration = _duration
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        switchRect.set(switchRect.left,height/2-heightView/2,it.animatedValue as Float,height/2+heightView/2)
                        invalidate()
                    }
                }
                AnimatorSet().apply {
                    play(leftAnim).with(rightAnim)
                    start()
                }
            }else{
                _state.value = 3
                val leftAnim = ValueAnimator.ofFloat(switchRect.left ,textPosition*2).apply {
                    duration = _duration
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        switchRect.set(it.animatedValue as Float,height/2-heightView/2,switchRect.right,height/2+heightView/2)
                        invalidate()
                    }
                }
                val rightAnim = ValueAnimator.ofFloat(switchRect.right, width.toFloat()).apply {
                    duration = _duration
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        switchRect.set(switchRect.left,height/2-heightView/2,it.animatedValue as Float,height/2+heightView/2)
                        invalidate()
                    }
                }
                AnimatorSet().apply {
                    play(leftAnim).with(rightAnim)
                    start()
                }
            }
        }

        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        baseRect.set(0F,height/2-heightView/2,width.toFloat(),height/2+heightView/2)
        switchRect.set(0F,height/2-heightView/2,width/3F,height/2+heightView/2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.addRoundRect(baseRect, 70f, 70f , Path.Direction.CW)
        canvas.drawPath(path, basePaint)
        canvas.clipPath(path)
        canvas.drawRoundRect(switchRect,70F, 70F, switchPaint)
        canvas.drawSwitchText()

    }

    private fun Canvas.drawSwitchText(){
        val fontMetrics = textPaint.fontMetrics
        val yPosition = heightView/2 + (abs(fontMetrics.ascent) - fontMetrics.descent)/2
        var textWidth = textPaint.measureText(leftText)
        var xPosition = textPosition/2 - textWidth/2
        drawText(leftText, xPosition, yPosition, textPaint)
        textWidth = textPaint.measureText(centerText)
        xPosition = (textPosition+textPosition/2) - textWidth/2
        drawText(centerText, xPosition, yPosition, textPaint)
        textWidth = textPaint.measureText(rightText)
        xPosition = (textPosition*2 + textPosition/2) - textWidth/2
        drawText(rightText, xPosition, yPosition, textPaint)
    }
}