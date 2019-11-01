package moe.aoramd.raindrop.view.base.binding

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import jp.wasabeef.picasso.transformations.BlurTransformation
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.repository.Tags
import java.lang.Exception

/**
 *  basic data binding adapter
 *
 *  @author M.D.
 *  @version dev 1
 */
object BindingBaseAdapter {

    /*
        image view
     */

    interface LoadImageUrlCallback {
        fun onComplete(bitmap: Bitmap?)
    }

    @JvmStatic
    @BindingAdapter("imageUrl", "imageUrlTarget", requireAll = false)
    fun loadImageFromUrl(
        imageView: ImageView,
        url: String?,
        target: Target?
    ) {
        if (url == null || url == Tags.UNKNOWN_TAG)
            imageView.setImageResource(R.drawable.img_placeholder)
        else {
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.img_placeholder)
                .into(imageView)

            target?.apply {
                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.img_placeholder)
                    .into(this)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("blurImageUrl")
    fun loadBlurImageFromUrl(
        imageView: ImageView,
        url: String
    ) {
        if (url == Tags.UNKNOWN_TAG)
            imageView.setImageResource(R.drawable.bg_blur_placeholder)
        else
            Picasso.get()
                .load(url)
                .transform(BlurTransformation(imageView.context, 25, 3))
                .placeholder(R.drawable.bg_blur_placeholder)
                .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("srcId")
    fun setImageViewResource(
        imageView: ImageView,
        resId: Int
    ) {
        imageView.setImageResource(resId)
    }

    /*
        swipe refresh layout
     */

    @JvmStatic
    @BindingAdapter("refreshing")
    fun setRefreshingState(
        swipeRefreshLayout: SwipeRefreshLayout,
        refreshing: Boolean
    ) {
        if (swipeRefreshLayout.isRefreshing != refreshing)
            swipeRefreshLayout.isRefreshing = refreshing
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "refreshing")
    fun getRefreshingState(swipeRefreshLayout: SwipeRefreshLayout): Boolean =
        swipeRefreshLayout.isRefreshing

    @JvmStatic
    @BindingAdapter("refreshingAttrChanged", "refreshingEvent", requireAll = false)
    fun refreshingStateListener(
        swipeRefreshLayout: SwipeRefreshLayout,
        listener: InverseBindingListener?,
        event: () -> Unit
    ) {
        listener?.let {
            swipeRefreshLayout.setOnRefreshListener {
                it.onChange()
                event.invoke()
            }
        }
    }

    /*
        recycler view
     */

    @JvmStatic
    @BindingAdapter("layoutManager")
    fun setRecyclerViewLayoutManager(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager
    ) {
        recyclerView.layoutManager = layoutManager
    }

    /*
        view
     */

    @JvmStatic
    @BindingAdapter("fadeVisible")
    fun changeVisibilityWithFadeAnimation(
        view: View,
        visibility: Int
    ) {
        when {
            (view.visibility == View.VISIBLE && visibility == View.GONE)
                    || (view.visibility == View.VISIBLE && visibility == View.INVISIBLE) -> {
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
                    duration = 200
                    doOnEnd {
                        view.visibility = visibility
                    }
                    start()
                }
            }
            (view.visibility == View.GONE && visibility == View.VISIBLE)
                    || (view.visibility == View.INVISIBLE && visibility == View.VISIBLE) -> {
                view.visibility = visibility
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                    duration = 200
                    start()
                }
            }
            else -> view.visibility = visibility
        }
    }

    @JvmStatic
    @BindingAdapter("onLongClick")
    fun setOnLongClickListener(
        view: View,
        listener: View.OnLongClickListener
    ) {
        view.setOnLongClickListener(listener)
    }
}