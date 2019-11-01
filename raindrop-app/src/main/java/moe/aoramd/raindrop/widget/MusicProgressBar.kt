package moe.aoramd.raindrop.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import moe.aoramd.raindrop.R
import kotlin.math.roundToInt

/**
 *  music progress bar
 *
 *  @constructor
 *  create new instance
 *
 *  @param context context
 *  @param attrs attributes
 *  @param defStyleAttr default style attributes
 *
 *  @author M.D.
 *  @version dev 1
 */
class MusicProgressBar(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    var playColor: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var bufferColor: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var backColor: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    private var oldPlayProgress: Int = 0
    var playProgress: Int = 0
        set(value) {
            if (field != value && value in 0..100) {
                field = value
                invalidate()
            }
        }
    var bufferProgress: Int = 100
        set(value) {
            if (field != value && value in 0..100) {
                field = value
                invalidate()
            }
        }

    var progressStartChangeListener: ProgressStartChangeListener? = null

    interface ProgressStartChangeListener {
        fun onStart()
    }

    var progressChangedListener: ProgressChangedListener? = null

    interface ProgressChangedListener {
        fun onChanged(progress: Int)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        val typeArray = context?.obtainStyledAttributes(attrs, R.styleable.MusicProgressBar)

        playColor =
            typeArray?.getColor(
                R.styleable.MusicProgressBar_playColor,
                ContextCompat.getColor(context, R.color.MusicProgressBarPlayColor)
            ) ?: 0xffbbdefb.toInt()
        bufferColor =
            typeArray?.getColor(
                R.styleable.MusicProgressBar_bufferColor,
                ContextCompat.getColor(context, R.color.MusicProgressBarBufferColor)
            ) ?: 0xffe3f2fd.toInt()
        backColor =
            typeArray?.getColor(
                R.styleable.MusicProgressBar_backColor,
                ContextCompat.getColor(context, R.color.MusicProgressBarBackColor)
            ) ?: 0xffffffff.toInt()

        typeArray?.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val left = paddingStart.toFloat()
        val top = paddingTop.toFloat()
        val bottom = (height - paddingBottom).toFloat()

        val playSize = playProgress * width / 100
        val bufferSize = bufferProgress * width / 100

        paint.color = backColor
        canvas?.drawRect(left, top, (width - paddingEnd).toFloat(), bottom, paint)
        paint.color = bufferColor
        canvas?.drawRect(left, top, (bufferSize + paddingStart).toFloat(), bottom, paint)
        paint.color = playColor
        canvas?.drawRect(left, top, (playSize + paddingStart).toFloat(), bottom, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    oldPlayProgress = playProgress
                    progressStartChangeListener?.onStart()
                }
                MotionEvent.ACTION_MOVE -> {
                    updateProgress(it.x)
                }
                MotionEvent.ACTION_UP -> {
                    updateProgress(it.x)
                    progressChangedListener?.onChanged(playProgress)
                }
                MotionEvent.ACTION_CANCEL -> {
                    playProgress = oldPlayProgress
                    progressChangedListener?.onChanged(playProgress)
                }
                else -> {
                }
            }
        }
        return true
    }

    private fun updateProgress(pos: Float) {
        val start = paddingStart
        val end = width - paddingEnd
        val range = end - start
        when {
            pos > start && pos < end -> playProgress =
                ((pos - start) * 100 / range).roundToInt()
            pos <= start -> playProgress = 0
            pos >= end -> playProgress = 100
        }
    }
}