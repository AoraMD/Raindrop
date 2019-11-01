package moe.aoramd.raindrop.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 *  square frame layout
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
class SquareFrameLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureSpec =
            if (MeasureSpec.getSize(widthMeasureSpec) <= MeasureSpec.getSize(heightMeasureSpec))
                widthMeasureSpec
            else
                heightMeasureSpec
        super.onMeasure(measureSpec, measureSpec)
    }
}