package com.lzp.zedittex

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Choreographer
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager

class ZEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text: String = ""
        set(value) {
            field = value
            invalidate()
        }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }

    private var mOnEditCompleteListener: OnEditCompleteListener? = null

    var textSize: Float = 50f
        set(value) {
            field = value
            mPaint.textSize = field
            invalidate()
        }

    var textColor: Int = Color.BLACK
        set(value) {
            field = value
            mPaint.color = field
            invalidate()
        }

    var passwordText: String = "●"
        set(value) {
            field = value
            invalidate()
        }

    var inputCount = 4
        set(value) {
            field = value
            invalidate()
        }

    var space = 20
        set(value) {
            field = value
            invalidate()
        }

    var isSquare = true
        set(value) {
            field = value
            requestLayout()
        }

    var textBackgroundDrawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    var isPassword = false
        set(value) {
            field = value
            invalidate()
        }

    var cursorDrawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    var cursorDrawableWidth = 0
        set(value) {
            field = value
            invalidate()
        }

    var cursorDrawableHeight = 0
        set(value) {
            field = value
            invalidate()
        }

    var isBoldText:Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 绘制cursor闪烁
     * */
    private var needDrawCursor = false

    init {
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true

        context.obtainStyledAttributes(attrs, R.styleable.ZEditText).apply {
            textSize = getDimension(R.styleable.ZEditText_android_textSize, 50f)
            textColor = getColor(R.styleable.ZEditText_android_textColor, Color.BLACK)
            inputCount = getInt(R.styleable.ZEditText_inputCount, 4)
            space = getDimensionPixelSize(R.styleable.ZEditText_space, 10)
            isSquare = getBoolean(R.styleable.ZEditText_isSquare, true)
            textBackgroundDrawable = getDrawable(R.styleable.ZEditText_textBackground)
            isPassword = getBoolean(R.styleable.ZEditText_isPassword, false)
            passwordText = getString(R.styleable.ZEditText_passwordText) ?: "●"
            isBoldText = getBoolean(R.styleable.ZEditText_isBoldText, false)
            cursorDrawable = getDrawable(R.styleable.ZEditText_cursorDrawable)
            cursorDrawableWidth =
                getDimensionPixelSize(R.styleable.ZEditText_cursorDrawableWidth, dip2px(1f))
            cursorDrawableHeight =
                getDimensionPixelSize(R.styleable.ZEditText_cursorDrawableHeight, textSize.toInt())
            needDrawCursor = cursorDrawable != null

            recycle()
        }

        mPaint.textSize = textSize
        mPaint.color = textColor
        mPaint.isFakeBoldText = isBoldText
    }

    fun setOnEditCompleteListener(listener: OnEditCompleteListener) {
        this.mOnEditCompleteListener = listener
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection? {
        outAttrs!!.inputType = EditorInfo.TYPE_CLASS_NUMBER
        return null
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    override fun isInEditMode(): Boolean {
        return isFocused
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isSquare) {
            val perWidth = (measuredWidth - space * (inputCount - 1)) / inputCount
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(perWidth, MeasureSpec.EXACTLY)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyPreIme(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6,
            KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9 -> {
                if (text.length < inputCount) {
                    text += event?.displayLabel
                    invalidate()

                    if (text.length == inputCount) {
                        mOnEditCompleteListener?.onEditComplete(text)
                    }
                }

                return true
            }
            KeyEvent.KEYCODE_DEL -> {
                if (!TextUtils.isEmpty(text)) {
                    text = text.substring(0, text.length - 1)
                    invalidate()
                }
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                hideSoft()
                mOnEditCompleteListener?.onEditComplete(text)
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            getInputMethodManager()?.let {
                if (isFocusable && !isFocused) {
                    requestFocus()
                }
                it.viewClicked(this)
                it.showSoftInput(this, 0)
//                it.restartInput(this)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getInputMethodManager(): InputMethodManager? {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private fun hideSoft() {
        getInputMethodManager()?.let {
            if (it.isActive(this)) {
                it.hideSoftInputFromWindow(windowToken, 0)
            }
        }
//        this.clearFocus()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val fontMetrics: Paint.FontMetrics = mPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom

        // 找到每一个字的位置
        val centerY = height / 2f
        val perWidth = (width - space * (inputCount - 1)) / inputCount
        for (index in 0 until inputCount) {
            drawTextBackground(canvas, index, perWidth)
            if (index < text.length) {
                val s = if (isPassword) {
                    passwordText
                } else {
                    text[index].toString()
                }
                val textWidth = mPaint.measureText(s)
                canvas.drawText(
                    s,
                    (perWidth * index + perWidth / 2 - textWidth / 2) + space * index,
                    centerY + distance,
                    mPaint
                )
            }
            if (index == text.length) {
                drawCursor(canvas, index, perWidth)
            }
        }
    }

    private fun drawTextBackground(
        canvas: Canvas,
        index: Int,
        perWidth: Int
    ) {
        if (textBackgroundDrawable == null) return
        val left = perWidth * index + space * index
        textBackgroundDrawable?.setBounds(
            left,
            0,
            left + perWidth,
            height
        )
        if (textBackgroundDrawable is StateListDrawable) {
            if (index == text.length && isFocused) {
                textBackgroundDrawable?.state = intArrayOf(android.R.attr.state_focused)
            } else {
                textBackgroundDrawable?.state = intArrayOf(android.R.attr.state_empty)
            }
            textBackgroundDrawable?.draw(canvas)
        } else {
            textBackgroundDrawable?.draw(canvas)
        }
    }

    private fun drawCursor(canvas: Canvas, index: Int, perWidth: Int) {
        if (!isInEditMode) {
            needDrawCursor = true
            return
        }
        cursorDrawable?.let {
            if (needDrawCursor) {
                val left = perWidth * index + space * index + perWidth / 2 - cursorDrawableWidth / 2
                it.setBounds(
                    left,
                    ((height - cursorDrawableHeight) / 2),
                    left + cursorDrawableWidth,
                    ((height + cursorDrawableHeight) / 2)
                )
                it.draw(canvas)
            }
            // 开始cursor动画
            Choreographer.getInstance().removeFrameCallback(drawCursorCallback)
            Choreographer.getInstance().postFrameCallbackDelayed(drawCursorCallback, 1000)
        }
    }

    private val drawCursorCallback = Choreographer.FrameCallback {
        needDrawCursor = !needDrawCursor
        invalidate()
    }

    private fun dip2px(dipValue: Float): Int {
        val displayMetricsDensity = context.resources.displayMetrics.density
        return (dipValue * displayMetricsDensity + 0.5f).toInt()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Choreographer.getInstance().removeFrameCallback(drawCursorCallback)
    }

    override fun setEnabled(enabled: Boolean) {
        if (enabled == isEnabled) {
            return
        }
        if (!enabled) {
            hideSoft()
        }
        super.setEnabled(enabled)
        if (enabled) {
            getInputMethodManager()?.restartInput(this)
        }
    }

    fun clear() {
        text = ""
    }

    fun setContent(text: String) {
        this.text = text
    }

    interface OnEditCompleteListener {
        fun onEditComplete(text: String)
    }
}