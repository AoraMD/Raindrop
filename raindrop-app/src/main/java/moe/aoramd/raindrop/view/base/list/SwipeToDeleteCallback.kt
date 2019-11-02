package moe.aoramd.raindrop.view.base.list

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import moe.aoramd.lookinglass.manager.ContextManager
import moe.aoramd.raindrop.R
import kotlin.math.min

/**
 *  swipe to delete callback for item touch helper
 *
 *  @property swipeListener invoked while item was swiped
 *
 *  @author M.D.
 *  @version dev 1
 */
class SwipeToDeleteCallback(private val swipeListener: (index: Int) -> Unit) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int
    ) = swipeListener.invoke(viewHolder.adapterPosition)

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val itemView = viewHolder.itemView

        val size = min(itemView.height shr 1, 120)
        val margin = (itemView.height - size) shr 1

        paint.color = ContextManager.resourceColor(R.color.swipeToDeleteBackgroundColor)
        canvas.drawRect(
            itemView.right.toFloat() + dX,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat(),
            paint
        )

        val icon = ContextManager.resourceDrawable(R.drawable.ic_delete)
        icon?.apply {
            setTint(ContextManager.resourceColor(R.color.colorAccent))
            setBounds(
                itemView.right - margin - size,
                itemView.top + margin,
                itemView.right - margin,
                itemView.bottom - margin
            )
            draw(canvas)
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}