package com.jaumard.instagrim.ui.gallery.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.jaumard.instagrim.R

class ImageCounterView : AppCompatImageView {
    private var textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var xTextPos: Float = 0.0f
    private var yTextPos: Float = 0.0f
    @ColorInt
    var textColor: Int = 0
        set(value) {
            field = value
            textPaint.color = textColor
            invalidate()
        }
    var text = ""
        set(value) {
            field = if (value.length >= 4) "99+" else value
            invalidate()
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (attrs != null) {
            val attributes = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.ImageCounterView,
                    0, 0)

            try {
                val counterText = attributes.getString(R.styleable.ImageCounterView_text)
                text = counterText ?: ""
                textColor = attributes.getColor(R.styleable.ImageCounterView_textColor, ContextCompat.getColor(context, R.color.colorAccent))
            } finally {
                attributes.recycle()
            }
        }
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11.5f, resources.displayMetrics)
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        xTextPos = widthSize / 2f
        yTextPos = (heightSize / 2f - (textPaint.descent() + textPaint.ascent()) / 2f)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //Draw the text content
        canvas.drawText(text, xTextPos, yTextPos, textPaint)
    }

}