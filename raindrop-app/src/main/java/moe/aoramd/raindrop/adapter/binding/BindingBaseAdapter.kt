package moe.aoramd.raindrop.adapter.binding

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.repository.Tags

object BindingBaseAdapter {

    @FunctionalInterface
    interface IndexClickListener {
        fun onClick(view: View, index: Int)
    }

    @JvmStatic
    @BindingAdapter("imageUrl", "loadUrlCallback", requireAll = false)
    fun loadImageFromUrl(
        imageView: ImageView,
        url: String?,
        callback: LoadImageUrlCallback?
    ) {
        if (url != null && url == Tags.OFFLINE_TAG)
            imageView.setImageResource(R.drawable.img_placeholder)
        else
            Glide.with(imageView.context)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.img_placeholder)
                .override(imageView.width, imageView.height)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean = false

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        callback?.onComplete(resource)
                        return false
                    }
                })
                .into(imageView)
    }

    interface LoadImageUrlCallback {
        fun onComplete(bitmap: Bitmap?)
    }

    @JvmStatic
    @BindingAdapter("blurImageUrl")
    fun loadBlurImageFromUrl(imageView: ImageView, url: String) {
        if (url == Tags.OFFLINE_TAG)
            imageView.setImageResource(R.drawable.img_placeholder)
        else
            Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.bg_blur_placeholder)
                .override(imageView.width, imageView.height)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("iconRes")
    fun setIconResource(imageView: ImageView, resId: Int) {
        imageView.setImageResource(resId)
    }

    @JvmStatic
    @BindingAdapter("refreshing")
    fun setRefreshingState(swipeRefreshLayout: SwipeRefreshLayout, refreshing: Boolean) {
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

    @JvmStatic
    @BindingAdapter("layoutManager")
    fun setRecyclerViewLayoutManager(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager
    ) {
        recyclerView.layoutManager = layoutManager
    }

    @JvmStatic
    @BindingAdapter("fadeVisible")
    fun changeVisibilityWithFadeAnimation(view: View, visibility: Int) {
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
}