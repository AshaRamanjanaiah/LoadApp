package com.udacity

import android.animation.AnimatorInflater
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var currentPercentage: Double = 0.0

    private val valueAnimator: ValueAnimator
    private val listener = ValueAnimator.AnimatorUpdateListener {
        currentPercentage = (it.animatedValue as Float).toDouble()

        invalidate()
    }

    private val circleRect = RectF()
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        textSize = 60.0f
        textAlign = Paint.Align.CENTER
    }

    private val DEFAULT_TEXT_COLOR = Color.WHITE
    private val DEFAULT_BACKGROUND_COLOR = resources.getColor(R.color.colorPrimary)

    private var textColor = DEFAULT_TEXT_COLOR
    private var buttonBackgroundColor = DEFAULT_BACKGROUND_COLOR


    init {

        valueAnimator = AnimatorInflater.loadAnimator(
            context, R.animator.loading_animation
        ) as ValueAnimator

        valueAnimator.addUpdateListener(listener)

        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton,
            0, 0)
        textColor = typedArray.getColor(R.styleable.LoadingButton_textColor, DEFAULT_TEXT_COLOR)
        buttonBackgroundColor = typedArray.getColor(R.styleable.LoadingButton_backgroundColor, DEFAULT_BACKGROUND_COLOR)

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            setSpace()
            drawRect(it)
            drawInnerArc(it)
            drawText(it)
        }

    }

    private fun setSpace() {
        val horizontalCenter = (width.div(2)).toFloat()
        val verticalCenter = (height.div(2)).toFloat()
        val circleSize = 50
        circleRect.set(
            horizontalCenter - circleSize,
            verticalCenter - circleSize,
            horizontalCenter + circleSize,
            verticalCenter + circleSize
        )

        circleRect.offset((horizontalCenter/2) + 30f , 0f)
    }

    private fun drawInnerArc(canvas: Canvas) {
        if (buttonState == ButtonState.Loading) {
            paint.color = Color.parseColor("#F9A825")
            canvas.drawArc(circleRect, 0f, (360 * (currentPercentage / 100)).toFloat(), true, paint)
        }
    }

    private fun drawText(canvas: Canvas) {
        val buttonText =
            if (buttonState == ButtonState.Loading)
                resources.getString(R.string.button_loading)
            else resources.getString(R.string.button_download)
        paint.color = textColor
        canvas.drawText(buttonText, widthSize.toFloat()/2, 100f, paint)
    }

    private fun drawRect(canvas: Canvas) {
        paint.strokeWidth = 0f
        paint.color = buttonBackgroundColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = Color.parseColor("#004349")
            canvas.drawRect(
                0f, 0f,
                (width * (currentPercentage / 100)).toFloat(), height.toFloat(), paint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

    }

    fun performButtonClick() {
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading
        valueAnimator.start()
    }

    fun hasCompletedDownload() {
        valueAnimator.cancel()

        buttonState = ButtonState.Completed
        invalidate()
    }

}